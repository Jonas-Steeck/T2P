package de.dhbw.WoPeDText2Process.UTToolwrapper;

import de.dhbw.WoPeDText2Process.wrapper.FrameNetFunctionality;
import de.dhbw.WoPeDText2Process.wrapper.FrameNetInitializer;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class UTFrameNet {
    @Test
    public void evaluateFramnetInvocation() throws IOException {

        /***check Initialiaztion***/
        FrameNetInitializer fni = FrameNetInitializer.getInstance();
        assertEquals("FrameNet Initialization Issue: Not initialized.", true, fni != null);

        /***check Functionality***/
        FrameNetFunctionality fnf = new FrameNetFunctionality();

        //TODO: Saubere Tests schreiben
        fnf.printAllFrames();
        //fnf.determineSpecifierFrameElement(fe, );
        //fnf.toPT(fe);
    }
}
