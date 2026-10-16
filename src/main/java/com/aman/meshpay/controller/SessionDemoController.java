package com.demo.upimesh.controller;

import com.demo.upimesh.model.*;
import com.demo.upimesh.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.*;

/** Rebuild a synthetic browser sandbox on any server instance using the real services. */
@RestController
@RequestMapping("/api/demo/session")
public class SessionDemoController {
    private static final Object SANDBOX_LOCK = new Object();
    private static final Set<String> VPAS = Set.of("alice@demo", "bob@demo", "carol@demo", "dave@demo");
    @Autowired private DemoService demo;
    @Autowired private MeshSimulatorService mesh;
    @Autowired private IdempotencyService idempotency;
    @Autowired private AccountRepository accounts;
    @Autowired private TransactionRepository transactions;
    @Autowired private ApiController api;

    public static class Command {
        public String type;
        public String packetId;
        public String senderVpa;
        public String receiverVpa;
        public BigDecimal amount;
    }

    @PostMapping
    public Map<String, Object> execute(@RequestBody List<Command> commands) throws Exception {
        if (commands.size() > 60) bad("This sandbox has reached 60 steps. Start a fresh sandbox.");
        for (Command c : commands) {
            if (c == null || c.type == null || !Set.of("send", "gossip", "flush", "reset").contains(c.type)) bad("Unknown sandbox step.");
            if ("send".equals(c.type)) {
                if (!VPAS.contains(Objects.toString(c.senderVpa,"")) || !VPAS.contains(Objects.toString(c.receiverVpa,"")) || c.senderVpa.equals(c.receiverVpa)) bad("Choose two different sandbox accounts.");
                if (c.amount == null || c.amount.signum() <= 0 || c.amount.compareTo(new BigDecimal("5000")) > 0 || c.amount.scale() > 2) bad("Use an amount from 0.01 to 5000 with at most two decimal places.");
                if (c.packetId == null) c.packetId = UUID.randomUUID().toString();
                try { UUID.fromString(c.packetId); } catch (IllegalArgumentException e) { bad("Invalid packet reference."); }
            }
        }
        synchronized (SANDBOX_LOCK) {
            // Each request is self-contained: no dependency on instance affinity,
            // shared caches or keys from a previous cold-started container.
            transactions.deleteAll();
            accounts.deleteAll();
            demo.seedAccounts();
            mesh.resetMesh();
            idempotency.clear();
            Object result = Map.of();
            for (Command c : commands) {
                switch (c.type) {
                    case "send" -> {
                        MeshPacket packet = demo.createPacket(c.senderVpa, c.receiverVpa, c.amount, "1234", 5);
                        packet.setPacketId(c.packetId);
                        mesh.inject("phone-alice", packet);
                        result = Map.of("packetId", packet.getPacketId(), "injectedAt", "phone-alice", "ttl", 5);
                    }
                    case "gossip" -> result = api.meshGossip();
                    case "flush" -> result = api.meshFlush();
                    case "reset" -> result = api.meshReset();
                }
            }
            return Map.of("commands", commands, "result", result, "mesh", api.meshState(),
                    "accounts", api.listAccounts(), "transactions", api.listTransactions());
        }
    }

    private static void bad(String message) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message); }
}
