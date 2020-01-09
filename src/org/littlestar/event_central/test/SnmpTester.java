/*
 * Copyright 2019 Li Xiang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.littlestar.event_central.test;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SnmpTester {
	public final int PORT = 162;
	private final Snmp snmp;
	private final String protocol;
	public SnmpTester(String protocol) throws Throwable {
		this.protocol = protocol;
		TransportMapping<?> transport;
		if (protocol.toLowerCase().equals("udp")) {
			transport = new DefaultUdpTransportMapping();
		} else {
			transport = new DefaultTcpTransportMapping();
		}
		snmp = new Snmp(transport);
	}

	public ResponseEvent send(PDU pdu, CommunityTarget target) throws Throwable {
		return snmp.send(pdu, target);
	}
	
	public ResponseEvent send(PDU pdu, String tAddr, int port, String community, int targetVersion) throws Throwable {
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString(community));
		Address addr = getSnmpAddress(protocol, tAddr, port);
		target.setAddress(addr);
		target.setVersion(targetVersion);
		return send(pdu, target);
	}
	
	public ResponseEvent send(PDU pdu, String tAddr, int port, String community) throws Throwable {
		return send(pdu, tAddr, port, community, SnmpConstants.version2c);
	}
	
	public ResponseEvent send(PDU pdu, String tAddr, String community) throws Throwable {
		return send(pdu, tAddr, PORT, community);
	}
	
	public ResponseEvent send(PDU pdu, String tAddr) throws Throwable {
		return send(pdu, tAddr, PORT, "public");
	}
	
	public static Address getSnmpAddress(String protocol, String address, int port) {
		String fullAddr = protocol + ":" + address + "/" + port;
		return GenericAddress.parse(fullAddr);
	}
}
