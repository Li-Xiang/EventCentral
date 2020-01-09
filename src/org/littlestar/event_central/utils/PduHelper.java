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

package org.littlestar.event_central.utils;

import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;

public class PduHelper {
	public static final String IBM_V7000_OID_PREFIX  = "1.3.6.1.4.1.2.6.190.4.";
	public static final String IBM_DS5000_OID_PREFIX = "1.3.6.1.4.1.789.1123.1.500.1.1.";
	public static final String IBM_HMC_OID_PREFIX    = "1.3.6.1.4.1.2.6.201.3";
	public static final String IBM_DS8000_PREFIX     = "1.3.6.1.4.1.2.6.130.3";
	public static final String FABRIC_OS_PREFIX      = "1.3.6.1.4.1.1588.2.1.1.1.8.5.1.";
	
	public static String getDigest(PDU pdu) {
		StringBuilder digest = new StringBuilder();
		for(VariableBinding vb: pdu.getVariableBindings()) {
			String oid   = vb.getOid().toString();
			String value = vb.getVariable().toString().trim();
			// IBM V7K
			boolean isIBMV7000 = false;
			if (oid.startsWith(IBM_V7000_OID_PREFIX)) {
				isIBMV7000 = true;
				if (oid.equals("1.3.6.1.4.1.2.6.190.4.1")) {
					// # Machine Type = 2076624
					String[] col = value.split("=");
					digest.append("M/T:").append(col[1].trim()).append(", ");
				} else if (oid.equals("1.3.6.1.4.1.2.6.190.4.2")) {
					// # Serial Number = 7824P8G
					String[] col = value.split("=");
					digest.append("M/S:").append(col[1].trim()).append(": ");
				} else if (oid.equals("1.3.6.1.4.1.2.6.190.4.3")) { // # Error ID = 981102 : SAS discovery occurred,...
					String[] col = value.split("=");
					digest.append(col[1].trim());
				}
				// IBM DS4K/5K
			} else if (oid.startsWith(IBM_DS5000_OID_PREFIX)) {
				if (oid.equals("1.3.6.1.4.1.789.1123.1.500.1.1.4")) {
					digest.append("DS4K/5K: ").append(value).append(", ");
				} else if (oid.equals("1.3.6.1.4.1.789.1123.1.500.1.1.7")) {
					digest.append(value).append("");
				}
				// IBM HMC
			} else if (oid.equals(IBM_HMC_OID_PREFIX)) {
				String vbStr = value.replaceAll("\n\r", "#").replaceAll("\n", "#");
				String[] vbArr = vbStr.split("#");
				for (String keyVal : vbArr) {
					if (keyVal.startsWith("FailingEnclosureMTMS")) {
						String[] col = keyVal.split("=");
						digest.append("MTMS: ").append(col[1]).append(", ");
					} else if (keyVal.startsWith("SRC")) {
						String[] col = keyVal.split("=");
						digest.append("SRC: ").append(col[1]).append(", ");
					} else if (keyVal.startsWith("EventText")) {
						String[] col = keyVal.split("=");
						digest.append(col[1]).append("");
					}
				}
				// IBM DS8K
			} else if (oid.equals(IBM_DS8000_PREFIX)) {
				String vbStr = value.replaceAll("\n\r", "#").replaceAll("\n", "#");
				String[] vbArr = vbStr.split("#");
				for (String keyVal : vbArr) {
					if (keyVal.startsWith("FailingEnclosureMTMS")) {
						String[] col = keyVal.split("=");
						digest.append("MTMS: ").append(col[1]).append(", ");
					} else if (keyVal.startsWith("SRC")) {
						String[] col = keyVal.split("=");
						digest.append("SRC: ").append(col[1]).append(", ");
					} else if (keyVal.startsWith("EventText")) {
						String[] col = keyVal.split("=");
						digest.append(col[1]).append("");
					}
				}
			// IBM SAN SWITCH
			} else if (oid.startsWith(FABRIC_OS_PREFIX)) {
				/************************************************
				[1.3.6.1.4.1.1588.2.1.1.1.8.5.1.1.1459]: 1459
				[1.3.6.1.4.1.1588.2.1.1.1.8.5.1.2.1459]: 2018/10/16-10:51:24
				[1.3.6.1.4.1.1588.2.1.1.1.8.5.1.3.1459]: 4
				[1.3.6.1.4.1.1588.2.1.1.1.8.5.1.4.1459]: 1
				[1.3.6.1.4.1.1588.2.1.1.1.8.5.1.5.1459]: SNMP-1005 SNMP configuration attribute, Trap Severity Level 4 , has changed from 4 to 5.
				 ***************************************************/
				if (oid.startsWith("1.3.6.1.4.1.1588.2.1.1.1.8.5.1.3.")) {
					switch(value) {
					case "1": value = "Critical(1)"; break;
					case "2": value = "Error(2)"; break;
					case "3": value = "Warning(3)"; break;
					case "4": value = "Info(4)"; break;
					default: value = " Unkonwn("+value+")"; break;
					}
					digest.append("SAN-SW:").append(value).append(", ");
				} else if(oid.startsWith("1.3.6.1.4.1.1588.2.1.1.1.8.5.1.5.")){
					digest.append(value);
				}
			} else { // UNKNOWN
				if(oid.startsWith("1.3.6.1.2.1.1.3.0")){
					// ignore sysUpTimeInstance
				}else if (oid.startsWith("1.3.6.1.6.3.1.1.4.1.0")) {
					// ignore snmpTrapOID(1);
				} else if (isIBMV7000) {
					// ignore IBM V7000's all others;
				} else {
					digest.append(value);
				}
			}
		}
		return digest.toString();
	}
	
	public static String truncString(String s, int max, String more) {
		if (s == null)
			return "";
		int length = s.length();
		if (length > max) {
			s = s.substring(0, (max - more.length()));
			s += more;
		}
		return s;
	}
	
	public static String truncString(String s, int max) {
		return truncString(s, max, "...");
	}
	
}
