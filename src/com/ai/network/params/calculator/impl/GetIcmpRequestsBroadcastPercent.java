package com.ai.network.params.calculator.impl;

import com.ai.network.params.calculator.NetworkParameterCalculator;
import org.pcap4j.packet.IcmpV4EchoPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;

import java.net.Inet4Address;
import java.util.Collection;
import java.util.Date;

/**
 * Created by victor on 23.11.15.
 */
public class GetIcmpRequestsBroadcastPercent implements NetworkParameterCalculator {
    @Override
    public double calculate(Collection<Packet> packets, Date start, Date end) {
        int packetsCount = packets.size();
        if (packetsCount == 0)
            return 0;
        int targetCount = 0;
        IcmpV4EchoPacket a;
        for (Packet packet : packets) {
            Packet pt = packet;
            boolean icmp = false;
            while (pt != null) {
                if (pt instanceof IcmpV4EchoPacket) {
                    icmp = true;
                    break;
                }
                pt = pt.getPayload();
            }
            if (icmp) {
                pt = packet;
                while (pt != null) {
                    if (pt.getHeader() instanceof IpV4Packet.IpV4Header) {
                        if (isBroadcast(((IpV4Packet.IpV4Header) pt.getHeader()).getDstAddr())) {
                            targetCount++;
                        }
                        break;
                    }
                    pt = pt.getPayload();
                }
            }
        }
        return ((double) targetCount / (double) packetsCount) * 100;
    }

    private boolean isBroadcast(Inet4Address address) {
        if (address.getAddress().length == 4) {
            if ((address.getAddress()[3] & 0xFF) == 255) {
                return true;
            }
        }
        return false;
    }
}
