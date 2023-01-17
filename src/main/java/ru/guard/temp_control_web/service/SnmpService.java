package ru.guard.temp_control_web.service;

import lombok.RequiredArgsConstructor;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.guard.temp_control_web.util.EventStatus;

import java.io.IOException;
import java.net.SocketException;

@Service
@RequiredArgsConstructor
public class SnmpService {
    @Value("${snmp-delta.port}")
    private String port;
    @Value("${snmp-delta.host}")
    private String host;
    @Value("${snmp-delta.oid}")
    private String oid;
    private String community = "public";
    private int snmpVersion = SnmpConstants.version1;
    private final EventService eventService;

    public String getOidTemperature() {
        Snmp snmp = getSnmp();
        CommunityTarget<Address> target = getTarget();
        PDU pdu = getPDU();

        ResponseEvent<Address> responseEvent;
        try {
            responseEvent = snmp.get(pdu, target);
        } catch (IOException e) {
            eventService.create("Snmp response event error: " + e.getMessage(), EventStatus.ERROR);
            throw new RuntimeException(e);
        }

        if (responseEvent != null) {
            PDU responsePDU = responseEvent.getResponse();
            return responsePDU.getVariableBindings().toString();
        }

        try {
            snmp.close();
        } catch (IOException e) {
            eventService.create("Snmp closing error: " + e.getMessage(), EventStatus.ERROR);
        }
        return "0";
    }

    private TransportMapping<UdpAddress> getTransport() {
        TransportMapping<UdpAddress> transport = null;

        try {
            transport = new DefaultUdpTransportMapping();
        } catch (SocketException e) {
            eventService.create("Snmp transport error: " + e.getMessage(), EventStatus.ERROR);
        }

        try {
            transport.listen();
        } catch (IOException e) {
            eventService.create("Snmp transport listen error: " + e.getMessage(), EventStatus.ERROR);
        }

        return transport;
    }

    private CommunityTarget<Address> getTarget() {
        CommunityTarget<Address> target = new CommunityTarget<>();
        target.setCommunity(new OctetString(community));
        target.setVersion(snmpVersion);
        target.setAddress(new UdpAddress(host + "/" + port));
        target.setRetries(2);
        target.setTimeout(1000);

        return target;
    }

    private PDU getPDU() {
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(oid)));
        pdu.setType(PDU.GET);
        pdu.setRequestID(new Integer32(1));

        return pdu;
    }

    private Snmp getSnmp() {
        TransportMapping<UdpAddress> transport = getTransport();

        return new Snmp(transport);
    }

}
