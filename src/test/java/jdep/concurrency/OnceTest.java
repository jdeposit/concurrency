/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package jdep.concurrency;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class OnceTest {

    @Test
    public void testBehavior() {
        Once<String> once = new Once<>();
        Assert.assertNull(once.get());
        Assert.assertTrue(once.set("1"));
        Assert.assertEquals("1", once.get());
        Assert.assertFalse(once.set("2"));
        Assert.assertEquals("1", once.get());
    }

    @Test
    public void testConcurrency() {
        Once<Integer> once = new Once<>();
        CountDownLatch counterOnStart = new CountDownLatch(1);
        CountDownLatch counterOnFinish = new CountDownLatch(2);
        AtomicBoolean firstSet = new AtomicBoolean();
        new Thread(() -> {
            await(counterOnStart);
            firstSet.compareAndSet(false, once.set(1));
            counterOnFinish.countDown();
        }).start();
        new Thread(() -> {
            await(counterOnStart);
            once.set(2);
            counterOnFinish.countDown();
        }).start();
        counterOnStart.countDown();
        await(counterOnFinish);

        Integer expectingResult = firstSet.get() ? 1 : 2;
        Assert.assertEquals(expectingResult, once.get());
    }

    private void await(CountDownLatch counter) {
        try {
            counter.await();
        } catch (InterruptedException e) {
            throw new IllegalStateException();
        }
    }

}
