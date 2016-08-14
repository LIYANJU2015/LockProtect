package com.example.lockprotect.lockprotectdemo;

import android.support.v4.util.ArrayMap;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);

        ArrayMap<Integer, Object> arrayMap = new ArrayMap<>();
        arrayMap.put(1, new Object());
        arrayMap.put(1, new Date());

        System.out.println(arrayMap.get(1));

    }
}