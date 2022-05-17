/*
 * Copyright (c) 2005, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package org.example;

import org.openjdk.jmh.annotations.Benchmark;
import org.example.MyHashSet.OpenAddressingSetIterator;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MyBenchmark {


    @State(Scope.Thread)
    public static class MyState {
        MyHashSet mySet = new MyHashSet(20);
        HashSet stockSet = new HashSet<Integer>();
        HashMap baseMap = new HashMap();
        Random random = new Random();

        @Setup(Level.Invocation)
        public void setup() {
            for (int i = 0; i < 100000; i++) {
                baseMap.put(i, random.nextInt(100000));
            }
        }
    }

    @Benchmark @BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MICROSECONDS) @Fork(value = 1)
    public void testStockSet(Blackhole bh, MyState state) {
        for (int i = 0; i < 50000; i++){
            state.stockSet.add(state.baseMap.get(i));
        }
        bh.consume(state.stockSet);
    }

    @Benchmark @BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MICROSECONDS) @Fork(value = 1)
    public void testMySet(Blackhole bh, MyState state) {
        for (int i = 0; i < 50000; i++){
            state.mySet.add(state.baseMap.get(i));
        }
        bh.consume(state.mySet);
    }


}
