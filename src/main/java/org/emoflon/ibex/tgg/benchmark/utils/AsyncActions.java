package org.emoflon.ibex.tgg.benchmark.utils;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.emoflon.ibex.tgg.benchmark.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Action extends Thread {

    private long waitTimeout;
    private Runnable action;

    /**
     * @param waitTimeout the waitTimeout to set
     */
    private void setWaitTimeout(long waitTimeout) {
        this.waitTimeout = waitTimeout;
    }

    /**
     * @param action the action to set
     */
    private void setAction(Runnable action) {
        this.action = action;
    }

    /**
     * @return the waitTimeout
     */
    public long getWaitTimeout() {
        return waitTimeout;
    }

    @Override
    public void run() {
        action.run();
    }

    public static void addAction(Runnable action, long waitTimeout, Vector<Action> actions) {
        Action newAction = new Action();
        newAction.setAction(() -> {
            try {
                action.run();
            } catch (Exception e) {
            }
            actions.remove(newAction);
        });
        newAction.setWaitTimeout(waitTimeout);

        actions.add(newAction);

        newAction.start();
    }

    public static void addUniqueAction(Runnable action, long waitTimeout, Hashtable<String, Action> actions, String id) {
        if (actions.containsKey(id))
            return;

        Action newAction = new Action();
        newAction.setAction(() -> {
            try {
                action.run();
            } catch (Exception e) {
            }
            actions.remove(id);
        });
        newAction.setWaitTimeout(waitTimeout);

        actions.put(id, newAction);

        newAction.start();
    }
}

/**
 * AsyncActions
 */
public class AsyncActions {

    private static final Logger LOG = LoggerFactory.getLogger(Core.PLUGIN_NAME);
    private static final Hashtable<String, Action> uniqueActions = new Hashtable<>();
    private static final Vector<Action> nonUniqueActions = new Vector<>();

    public static void runAction(Runnable action) {
        runAction(action, 0);
    }

    public static void runAction(Runnable action, long waitTimeout) {
        Action.addAction(action, waitTimeout, nonUniqueActions);
    }

    public static void runUniqueAction(Runnable action, String actionId) {
        runUniqueAction(action, 0, actionId);
    }

    public static void runUniqueAction(Runnable action, long waitTimeout, String actionId) {
        Action.addUniqueAction(action, waitTimeout, uniqueActions, actionId);
    }

    public static void stopAll() {
        long referenceTime = System.currentTimeMillis();

        List<Action> unfinishedActions = Collections.list(uniqueActions.elements());
        unfinishedActions.addAll(nonUniqueActions);

        for (Action action : unfinishedActions) {
            while (action.isAlive()) {
                action.interrupt();
            }
        }

        try {
            for (Action action : unfinishedActions) {
                while (action.isAlive()) {
                    long timeWaited = System.currentTimeMillis() - referenceTime;
                    if (timeWaited > action.getWaitTimeout()) {
                        LOG.warn("Action didn't finish in time, killing it now");
                        action.stop();
                    } else {
                        Thread.sleep(333);
                        continue;
                    }
                }
            }
        } catch (InterruptedException e) {
        }
    }
}