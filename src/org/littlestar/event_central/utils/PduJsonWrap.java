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

import java.util.Vector;

import org.snmp4j.PDU;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PduJsonWrap {
	@SerializedName("variableBindings")
	@Expose
    protected Vector<VbPair> variableBindingPairs ;
	
	@SerializedName("errorStatus")
	@Expose
    protected Integer errorStatus ;
	
	@SerializedName("errorIndex")
	@Expose
    protected Integer errorIndex ;
	
	@SerializedName("requestID")
	@Expose
    protected Integer requestID ;
	
	@SerializedName("type")
	@Expose
    protected int type ;
	
	public PduJsonWrap(PDU pdu) {
		type = pdu.getType();
		errorStatus = pdu.getErrorStatus();
		errorIndex = pdu.getErrorIndex();
		requestID = pdu.getRequestID().toInt();
		variableBindingPairs = toVariableBindingPairs(pdu.getVariableBindings());
	}
	
	public PDU getPDU() {
		PDU pdu = new PDU();
		pdu.setRequestID(new Integer32(requestID));
		pdu.setType(PDU.NOTIFICATION);
		pdu.setErrorStatus(errorStatus);
		pdu.setVariableBindings(toVariableBindings(variableBindingPairs));
		return pdu;
	}
	
	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	private Vector<? extends VariableBinding> toVariableBindings(Vector<VbPair> variableBindings2){
		 Vector<VariableBinding> vbs = new Vector<VariableBinding>();
		 for(VbPair vbPair: variableBindings2) {
			 OID oid = new OID(vbPair.getOid());
			 OctetString value = new OctetString(vbPair.getValue());
			 vbs.add(new VariableBinding(oid, value));
		 }
		 return vbs;
	}
	
	private Vector<VbPair> toVariableBindingPairs( Vector<? extends VariableBinding> variableBindings) {
		Vector<VbPair> vbPairs = new Vector<VbPair>();
		for(VariableBinding vb:variableBindings) {
			vbPairs.add(new VbPair(vb));
		}
		return vbPairs;
	}
	
	
	private static class VbPair {
		@SerializedName("oid")
		@Expose
		public String oid;
		@SerializedName("variableValue")
		@Expose
		public String value;
		
		public VbPair(VariableBinding vb) {
			oid = vb.getOid().toString();
			value = vb.getVariable().toString().trim();
		}
		
		public String getValue() {
			return value;
		}
		
		public String getOid() {
			return oid;
		}
	}
}
