package com.empcraft;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class ProtocolClass {
    InSignsPlus ISP;
    ProtocolManager protocolmanager = null;
    JSONParser parser = new JSONParser();
    
    /**
     * Evaluate json array (can be used for chat / signs etc)
     * @param input
     * @param loc
     */
    @SuppressWarnings({ "unchecked" })
    public void evaluateAll(final Object input, Location loc) {
        if ((input instanceof JSONObject)) {
            final JSONObject object = (JSONObject) input;
            for (final Object k : object.keySet()) {
                final Object value = object.get(k);
                if ((value instanceof JSONObject)) {
                    evaluateAll(value, loc);
                } else if ((value instanceof JSONArray)) {
                    evaluateAll(value, loc);
                } else if ((value instanceof String)) {
                    final String string = value.toString();
                    object.put(k, ISP.colorise(ISP.evaluate(string, false, loc)));
                }
            }
        } else if ((input instanceof JSONArray)) {
            final JSONArray array = (JSONArray) input;
            for (int i = 0; i < array.size(); i++) {
                final Object value = array.get(i);
                if ((value instanceof JSONObject)) {
                    evaluateAll(value, loc);
                } else if ((value instanceof JSONArray)) {
                    evaluateAll(value, loc);
                } else if ((value instanceof String)) {
                    final String string = value.toString();
                    array.set(i, ISP.colorise(ISP.evaluate(string, false, loc)));
                }
            }
        }
    }
    
    public void writePacket(PacketContainer packet, String[] lines) {
        WrappedChatComponent[] component = new WrappedChatComponent[4];
        for (int j = 3; j >= 0; j--) {
            if (!lines[j].equals("")) {
                component[j] = WrappedChatComponent.fromJson(lines[j]);
            }
        }
        packet.getChatComponentArrays().write(0, component);
    }
    
    public ProtocolClass(InSignsPlus plugin) {
        ISP = plugin;
        protocolmanager = ProtocolLibrary.getProtocolManager();
        
        protocolmanager.addPacketListener(new PacketAdapter(ISP, ListenerPriority.LOW, PacketType.Play.Server.UPDATE_SIGN) {
            public void onPacketSending(PacketEvent event) {
                ISP.recursion = 0;
                PacketContainer oldpacket = event.getPacket();
                PacketContainer packet = oldpacket.shallowClone();
                BlockPosition block = packet.getBlockPositionModifier().getValues().get(0);
                
                int packetx = block.getX();
                int packety = block.getY();
                int packetz = block.getZ();
                
                Player player = event.getPlayer();
                Location loc = new Location(player.getWorld(), packetx, packety, packetz);
                ISP.setUser(player);
                ISP.setSender(player);
                WrappedChatComponent[] component = packet.getChatComponentArrays().read(0);
                evaluateAll(component, loc);
                String[] lines = new String[4];
                boolean changed = false;
                boolean[] whitelist = new boolean[] {false, false, false, false };
                boolean whitelisted = false;
                for (int j = 0; j < 4; j++) {
                    String line = component[j].getJson();
                    if (line == null) {
                        lines[j] = "";
                    }
                    else if (!line.equals("")) {
                        Object json;
                        try {
                            json = parser.parse(component[j].getJson());
                        } catch (ParseException e) {
                            e.printStackTrace();
                            lines[j] = "";
                            continue;
                        }
                        evaluateAll(json, loc);
                        if (ISP.iswhitelisted(line)) {
                            whitelist[j] = true;
                            whitelisted = true;
                        }
                        String newline = json.toString();
                        if (!newline.equals(line)) {
                            changed = true;
                        }
                        lines[j] = newline;
                    }
                    else {
                        lines[j] = "";
                    }
                }
                if (!changed && !whitelisted) {
                    ISP.setUser(null);
                    ISP.setSender(null);
                    return;
                }
                if (whitelisted) {
                    PlayerSign ps = new PlayerSign(player.getName(), loc);
                    SignPlus sp = ISP.updateMap.get(ps);
                    if (sp == null) {
                        // add to queue
                        ISP.addUpdateQueue(player, loc, whitelist);
                        ISP.setUser(null);
                        ISP.setSender(null);
                    }
                    // send update
                    writePacket(packet, lines);
                    event.setPacket(packet);
                }
                else if (changed) {
                    // send update
                    writePacket(packet, lines);
                    event.setPacket(packet);
                }
                ISP.setUser(null);
                ISP.setSender(null);
            }
        });
    }
}
