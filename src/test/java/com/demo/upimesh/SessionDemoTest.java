package com.demo.upimesh;

import com.demo.upimesh.controller.SessionDemoController;
import com.demo.upimesh.controller.SessionDemoController.Command;
import com.demo.upimesh.model.Account;
import com.demo.upimesh.model.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestExecutionListeners(listeners = DependencyInjectionTestExecutionListener.class, inheritListeners = false)
class SessionDemoTest {
    @Autowired private SessionDemoController controller;
    private Command step(String type) { Command c = new Command(); c.type = type; return c; }
    @SuppressWarnings("unchecked")
    private BigDecimal alice(Map<String,Object> snapshot) {
        return ((List<Account>) snapshot.get("accounts")).stream().filter(a -> "alice@demo".equals(a.getVpa())).findFirst().orElseThrow().getBalance();
    }
    @Test
    @SuppressWarnings("unchecked")
    void historySurvivesInstanceStateLossAndKeepsSessionsIndependent() throws Exception {
        Command send=step("send");send.senderVpa="alice@demo";send.receiverVpa="bob@demo";send.amount=new BigDecimal("500.00");
        List<Command> history=new ArrayList<>(List.of(send,step("gossip"),step("flush")));
        var first=controller.execute(history);
        String packetId=send.packetId;
        assertEquals(0,new BigDecimal("4500").compareTo(alice(first)));
        assertEquals(1,((List<Transaction>)first.get("transactions")).size());
        // Another visitor or fresh instance starts from its own synthetic balances.
        assertEquals(0,new BigDecimal("5000").compareTo(alice(controller.execute(List.of()))));
        history.add(step("flush"));
        var replayed=controller.execute(history);
        assertEquals(packetId,send.packetId);
        assertEquals(0,new BigDecimal("4500").compareTo(alice(replayed)));
        assertEquals(1,((List<Transaction>)replayed.get("transactions")).size());
        var result=(Map<String,Object>)replayed.get("result");
        var results=(List<Map<String,Object>>)result.get("results");
        assertEquals("DUPLICATE_DROPPED",results.get(0).get("outcome"));
    }
}
