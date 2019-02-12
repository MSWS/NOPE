# AntiCheat
### Permissions
```java
public boolean bypassCheck(Check check) {
	if (!player.isOnline())
		return false;
	Player online = (Player) player;
	if (online.hasPermission("anticheat.bypass." + check.getType()))
		return true;
	if (online.hasPermission("anticheat.bypass." + check.getCategory()))
		return true;
	if (online.hasPermission("anticheat.bypass." + check.getType() + "." + check.getCategory()))
		return true;
	return online.hasPermission("anticheat.bypass." + check.getType() + "." + check.getDebugName());
}
```
