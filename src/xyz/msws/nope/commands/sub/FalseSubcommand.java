package xyz.msws.nope.commands.sub;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueBuilder;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.HttpException;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.commands.CommandResult;
import xyz.msws.nope.commands.Subcommand;
import xyz.msws.nope.listeners.TokenCreationListener;
import xyz.msws.nope.utils.MSG;

public class FalseSubcommand extends Subcommand {

	private GitHub git;
	private GHRepository repo;

	@SuppressWarnings("deprecation")
	public FalseSubcommand(NOPE plugin) {
		super(plugin);

		String user = plugin.getOption("gusername").getValue(), pass = plugin.getOption("gpassword").getValue();

		if (user.isEmpty())
			return;

		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			try {
				if (pass.isEmpty()) {
					MSG.log("Attempting to login to GitHub via PAT");
					git = GitHub.connect("NOPE", user);
				} else {
					MSG.warn("It is strongly recommended to use a PAT instead of username/password!");
					MSG.warn(
							"See https://docs.github.com/en/github/authenticating-to-github/creating-a-personal-access-token");
					git = GitHub.connectUsingPassword(user, pass);
				}
				repo = git.getRepository("MSWS/NOPE");
			} catch (HttpException e) {
				MSG.log("An error occured when attempting to authenticate with GitHub: " + e.getResponseCode());
				MSG.log(e.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public List<String[]> tabCompletions(CommandSender sender, String[] args) {
		List<String[]> result = new ArrayList<>();
		result.add(plugin.getModule(TokenCreationListener.class).getTokens().toArray(new String[0]));
		return result;
	}

	@Override
	public String getName() {
		return "false";
	}

	@Override
	public String getUsage() {
		return "[token]";
	}

	@Override
	public String getDescription() {
		return "report a false ban";
	}

	@Override
	public CommandResult execute(CommandSender sender, String[] args) {
		if (args.length != 2)
			return CommandResult.MISSING_ARGUMENT;
		if (git == null || repo == null) {
			MSG.tell(sender, MSG.getString("Command.FalsePositive.Disabled", "Unable to report"));
			return CommandResult.SUCCESS;
		}

		String id = args[1];

		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			MSG.tell(sender, MSG.getString("Command.FalsePositive.Checking", "Checking duplicate reports..."));
			try {
				if (repo.getIssues(GHIssueState.ALL).parallelStream().anyMatch(i -> i.getTitle().contains(id))) {
					MSG.tell(sender, MSG.getString("Command.FalsePositive.Duplicate", "You have already reported %id%")
							.replace("%id%", id));
					return;
				}

				List<GHIssue> issues = repo.getIssues(GHIssueState.OPEN);
				long open = (long) issues.parallelStream().filter(t -> {
					try {
						return t.getUser().getId() == git.getMyself().getId();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return false;
				}).count();

				if (open >= 5) {
					MSG.tell(sender, "You have too many open false positive reports.");
					return;
				}
			} catch (IOException e2) {
				e2.printStackTrace();
				MSG.tell(sender, CommandResult.ERROR.getMessage());
				return;
			}

			MSG.tell(sender, MSG.getString("Command.FalsePositive.Querying", "Gathering ban logs..."));
			List<String> result = getLogs(id);
			if (result == null || result.isEmpty()) {
				MSG.tell(sender, CommandResult.INVALID_ARGUMENT.getMessage());
				return;
			}

			GHIssueBuilder builder = repo.createIssue("FP: " + id);
			List<String> data = new ArrayList<>();
			data.add("# Sources\n" + "Reporter:       `" + sender.getName() + "`  \n" + "Online Players: `" + Bukkit.getOnlinePlayers().size() + "`  \n" + "Owner:          `" + Bukkit.getOfflinePlayers()[0].getName() + "`  \n");

			StringJoiner pbuilder = new StringJoiner("\n");
			for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
				pbuilder.add(p.getName() + ": " + p.getDescription().getVersion());
			}
			data.add("# Plugins\n" + "```\n" + pbuilder.toString() + "\n```");

			data.addAll(result);

			File file = plugin.getConfigFile();
			try {
				StringBuilder sb = new StringBuilder();
				FileReader reader = new FileReader(file);
				BufferedReader breader = new BufferedReader(reader);
				String line;

				sb.append("\n# Config\n```yaml\n");
				while ((line = breader.readLine()) != null) {
					if(line.contains("Username") || line.contains("Password")) continue;
					sb.append(line).append("\n");
				}
				sb.append("```");
				data.add(sb.toString());
				breader.close();
				reader.close();
			} catch (IOException e1) {
				data.add("Unable to read config: " + e1.getMessage());
				e1.printStackTrace();
			}

			MSG.tell(sender, MSG.getString("Command.FalsePositive.Reporting", "Reporting..."));

			builder.body(String.join("\n", data));
			builder.label("False Positive");
			try {
				builder.create();
				MSG.tell(sender,
						MSG.getString("Command.FalsePositive.Success", "Successfully reported %id% as a false positive")
								.replace("%id%", id));
			} catch (IOException e) {
				e.printStackTrace();
				MSG.tell(sender, CommandResult.ERROR.getMessage());
			}
		});
		return CommandResult.SUCCESS;
	}

	List<String> getLogs(String id) {
		if (id.length() == 16) {
			File logs = new File(plugin.getDataFolder(), "logs");
			File log = new File(logs, id + ".log");
			if (!log.exists())
				return null;

			List<String> result = new ArrayList<>();

			try {
				FileReader fread = new FileReader(log);
				BufferedReader reader = new BufferedReader(new FileReader(log));

				String line;
				result.add("\n# Flag Log\n```");
				while ((line = reader.readLine()) != null)
					result.add(line.trim());
				result.add("\n```");
				reader.close();
				fread.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}

		List<String> result = new ArrayList<>();
		try {
			URL url = new URL("https://hastebin.com/raw/" + id);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-Agent", "NOPE/MC-" + plugin.getDescription().getVersion());
			InputStreamReader iread = new InputStreamReader(conn.getInputStream());
			BufferedReader reader = new BufferedReader(iread);
			String line;

			while ((line = reader.readLine()) != null)
				result.add(line.trim());

			reader.close();
			iread.close();
		} catch (IOException e) {
			return null;
		}

		return result;
	}

}
