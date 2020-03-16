package com.example.sanskriti.odml;

import com.example.sanskriti.odml.Authentication.Login;
import static org.mockito.Mockito.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.security.auth.callback.Callback;

import static org.junit.Assert.assertEquals;

public class login_unit_test {
    private Login loginTestOb;
    @Before //This is executed before the @Test executes
    public void setUp(){
        loginTestOb = new Login();
        System.out.println("Ready for testing");
    }
    @After //This is executed after the @Test executes
    public void tearDown(){
        System.out.println("Done with testing");
    }
    @Test
    public void givenServiceWithValidResponse_whenCallbackReceived_thenProcessed() {
        Callback myMock = mock(Callback.class);

    }
//    @Test
//    public void testDiff() {
//        int total = simpleMath.diff(9, 2);
//        assertEquals("Simple Math is not subtracting correctly", 7, total);
//    }
//    @Test
//    public void testDiv(){
//        double quotient = simpleMath.div(9,3);
//        assertEquals("Simple math is not dividing correctly", 3.0, quotient, 0.0);
//    }
//    //@Ignore //This ignores the test below
//    @Test
//    public void testDivWithZeroDivisor(){
//        double quotient = simpleMath.div(9,0);
//        assertEquals("Simple math is not handling division by zero correctly", 0.0, quotient, 0.0);
//    }
}