/**
 * PacketWrapper - ProtocolLib wrappers for Minecraft packets
 * Copyright (C) dmulloy2 <http://dmulloy2.net>
 * Copyright (C) Kristian S. Strangeland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Acest program este distribuit în speranța că va fi util,
 * dar fără nicio GARANȚIE; fără nici măcar garanția implicită de
 * MERCHANTABILITATEA sau dotările în scopuri speciale.  Vezi
 * GNU General Public License pentru mai multe detalii.
 *
 * Ar fi trebuit să primiți o copie a licenței GNU General Public License
 * împreună cu acest program.  Dacă nu, vezi <http://www.gnu.org/licenses/>.
 */
xyz.msws.nope.protocols;

import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

Clasa publică WrapperPlayServerRelEntityMove extinde AbstractPacket {
	Public static final PacketType TYPE = PacketType.Play.Server.REL_ENTITY_MOVE;

	public WrapperPlayServerRelEntityMove() {
		super(ambalaj nou(nou) Container (TYPE), TYPE);
		handle.getModifier().writeDefaults();
	}

	public WrapperPlayServerRelEntityMove(PacketContainer packet) {
		super(pachet, TIPE);
	}

	/**
	 * Recuperează ID-ul Entității.
	 * <p>
	 * Note: ID-ul entității
	 * 
	 * @return ID-ul entității curente
	 */
	public getEntityID() {
		return handle.getIntegers().read(0);
	}

	/**
	 * Setează ID-ul de entitate.
	 * 
	 * Valoare @param - valoare nouă.
	 */
	public void setEntityID(int value) {
		handle.getIntegers().write(0, valoare);
	}

	/**
	 * Recuperează entitatea picturii care va fi generată.
	 * 
	 * Lumea @param - lumea curentă a entității.
	 * @return Entitatea creată.
	 */
	public Entity getEntity(World world) {
		return handle.getEntityModifier(world).read(0);
	}

	/**
	 * Recuperează entitatea picturii care va fi generată.
	 * 
	 * Evenimentul @param - evenimentul pachetului.
	 * @return Entitatea creată.
	 */
	public Entity getEntity(PacketEvent event) {
		return getEntity(event.getPlayer().getWorld());
	}

	/**
	 * Recuperează DX.
	 *
	 * @return DX curent
	 */
	public dublu getDx() {
		handle.getShorts().read(0) / 4096D;
	}

	/**
	 * Setează DX.
	 *
	 * Valoare @param - valoare nouă.
	 */
	evitați public setDx(valoare dublă) {
		handle.getShorts().write(0, (scurt) (valoare * 4096));
	}

	/**
	 * Recuperează DY.
	 *
	 * @return DY curent
	 */
	public dublu getDy() {
		handle.getShorts().read(1) / 4096D;
	}

	/**
	 * Setează ZY.
	 *
	 * Valoare @param - valoare nouă.
	 */
	anulați public setDy(valoare dublă) {
		handle.getShorts().write(1, (scurt) (valoare * 4096));
	}

	/**
	 * Recuperează DZ.
	 *
	 * @return DZ curent
	 */
	public dublu getDz() {
		handle.getShorts().read(2) / 4096D;
	}

	/**
	 * Setează ZA.
	 *
	 * Valoare @param - valoare nouă.
	 */
	evitați public setDz(valoare dublă) {
		handle.getShorts().write(2, (scurt) (valoare * 4096));
	}

	/**
	 * Recuperează catapulta entității curente.
	 *
	 * @return Yaw curent
	 */
	public float getYaw() {
		return (handle.getBytes().read(0) * 360.F) / 256.0F;
	}

	/**
	 * Setaţi yaw entităţii curente. (Automatic Translation)
	 *
	 * Valoare @param - yawul nou.
	 */
	evitați public setYaw(float valoare) {
		handle.getBytes().write(0, (byte) (valoare * 256.0F / 360.0F));
	}

	/**
	 * Recuperează avansul entității curente.
	 *
	 * @return Muntea curentă
	 */
	float public getPitch() {
		return (handle.getBytes().read(1) * 360.F) / 256.0F;
	}

	/**
	 * Setaţi avansul entităţii curente. (Automatic Translation)
	 *
	 * Valoare @param - pas nou.
	 */
	Evitați public setPitch(float valoare) {
		handle.getBytes().write(1, (byte) (valoare * 256,0F / 360,0F));
	}

	/**
	 * Recuperează pe teren.
	 *
	 * @return curentul pe teren
	 */
	boolean public getOnGround() {
		handle.getBooleans().read(0);
	}

	/**
	 * Setează pe teren.
	 *
	 * Valoare @param - valoare nouă.
	 */
	evitați public setOnGround(valoare booleană) {
		handle.getBooleans().write(0, valoare);
	}
}