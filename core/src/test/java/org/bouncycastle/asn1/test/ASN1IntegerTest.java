package org.bouncycastle.asn1.test;

import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.test.SimpleTest;

public class ASN1IntegerTest
    extends SimpleTest
{
    public String getName()
    {
        return "ASN1Integer";
    }

    public void performTest()
        throws Exception
    {
        testValidEncodingSingleByte();
        testValidEncodingMultiByte();
        testInvalidEncoding_00();
        testInvalidEncoding_ff();
        testInvalidEncoding_00_32bits();
        testInvalidEncoding_ff_32bits();
        testLooseInvalidValidEncoding_FF_32B();
        testLooseInvalidValidEncoding_zero_32B();
        testLooseValidEncoding_zero_32BAligned();
        testLooseValidEncoding_FF_32BAligned();
        testLooseValidEncoding_FF_32BAligned_1not0();
        testLooseValidEncoding_FF_32BAligned_2not0();
        
        System.setProperty("org.bouncycastle.asn1.allow_unsafe_integer", "true");

        new ASN1Integer(Hex.decode("ffda47bfc776bcd269da4832626ac332adfca6dd835e8ecd83cd1ebe7d709b0e"));

        new ASN1Enumerated(Hex.decode("ffda47bfc776bcd269da4832626ac332adfca6dd835e8ecd83cd1ebe7d709b0e"));

        try
        {
            new ASN1Integer(Hex.decode("ffda47bfc776bcd269da4832626ac332adfca6dd835e8ecd83cd1ebe7d709b"));

            fail("no exception");
        }
        catch (IllegalArgumentException e)
        {
            isEquals("malformed integer", e.getMessage());
        }

        System.setProperty("org.bouncycastle.asn1.allow_unsafe_integer", "false");

        try
        {
            new ASN1Integer(Hex.decode("ffda47bfc776bcd269da4832626ac332adfca6dd835e8ecd83cd1ebe7d709b0e"));

            fail("no exception");
        }
        catch (IllegalArgumentException e)
        {
            isEquals("malformed integer", e.getMessage());
        }

        try
        {
            new ASN1Enumerated(Hex.decode("ffda47bfc776bcd269da4832626ac332adfca6dd835e8ecd83cd1ebe7d709b0e"));

            fail("no exception");
        }
        catch (IllegalArgumentException e)
        {
            isEquals("malformed enumerated", e.getMessage());
        }
    }

    /**
     * Ensure existing single byte behavior.
     */
    public void testValidEncodingSingleByte()
        throws Exception
    {
        System.setProperty("org.bouncycastle.asn1.allow_unsafe_integer", "false");
        //
        // Without property, single byte.
        //
        byte[] rawInt = Hex.decode("10");
        ASN1Integer i = new ASN1Integer(rawInt);
        isEquals(i.getValue().intValue(), 16);

        //
        // With property set.
        //
        System.setProperty("org.bouncycastle.asn1.allow_unsafe_integer", "true");

        rawInt = Hex.decode("10");
        i = new ASN1Integer(rawInt);
        isEquals(i.getValue().intValue(), 16);

    }

    public void testValidEncodingMultiByte()
        throws Exception
    {
        System.setProperty("org.bouncycastle.asn1.allow_unsafe_integer", "false");
        //
        // Without property, single byte.
        //
        byte[] rawInt = Hex.decode("10FF");
        ASN1Integer i = new ASN1Integer(rawInt);
        isEquals(i.getValue().intValue(), 4351);

        //
        // With property set.
        //
        System.setProperty("org.bouncycastle.asn1.allow_unsafe_integer", "true");

        rawInt = Hex.decode("10FF");
        i = new ASN1Integer(rawInt);
        isEquals(i.getValue().intValue(), 4351);

    }

    public void testInvalidEncoding_00()
        throws Exception
    {
        System.setProperty("org.bouncycastle.asn1.allow_unsafe_integer", "false");
        try
        {
            byte[] rawInt = Hex.decode("0010FF");
            ASN1Integer i = new ASN1Integer(rawInt);
            isEquals(i.getValue().intValue(), 4351);
            fail("Expecting illegal argument exception.");
        }
        catch (Throwable t)
        {
            isEquals("Expected decoder exception.", IllegalArgumentException.class, t.getClass());
        }
    }

    public void testInvalidEncoding_ff()
        throws Exception
    {
        System.setProperty("org.bouncycastle.asn1.allow_unsafe_integer", "false");
        try
        {
            byte[] rawInt = Hex.decode("FF81FF");
            ASN1Integer i = new ASN1Integer(rawInt);
            fail("Expecting illegal argument exception.");
        }
        catch (Throwable t)
        {
            isEquals("Expected decoder exception.", IllegalArgumentException.class, t.getClass());
        }
    }

    public void testInvalidEncoding_00_32bits()
        throws Exception
    {
        System.setProperty("org.bouncycastle.asn1.allow_unsafe_integer", "false");
        //
        // Check what would pass loose validation fails outside of loose validation.
        //
        try
        {
            byte[] rawInt = Hex.decode("0000000010FF");
            ASN1Integer i = new ASN1Integer(rawInt);
            isEquals(i.getValue().intValue(), 4351);
            fail("Expecting illegal argument exception.");
        }
        catch (Throwable t)
        {
            isEquals("Expected decoder exception.", IllegalArgumentException.class, t.getClass());
        }
    }

    public void testInvalidEncoding_ff_32bits()
        throws Exception
    {
        System.setProperty("org.bouncycastle.asn1.allow_unsafe_integer", "false");
        //
        // Check what would pass loose validation fails outside of loose validation.
        //
        try
        {
            byte[] rawInt = Hex.decode("FFFFFFFF01FF");
            ASN1Integer i = new ASN1Integer(rawInt);
            fail("Expecting illegal argument exception.");
        }
        catch (Throwable t)
        {
            isEquals("Expected decoder exception.", IllegalArgumentException.class, t.getClass());
        }
    }

    public void testLooseInvalidValidEncoding_zero_32B()
        throws Exception
    {
        System.setProperty("org.bouncycastle.asn1.allow_unsafe_integer", "false");
        //
        // Should still fail as loose validation only permits 3 leading 0x00 bytes.
        //
        try
        {
            System.getProperties().put("org.bouncycastle.asn1.allow_unsafe_integer", "true");
            byte[] rawInt = Hex.decode("0000000010FF");
            ASN1Integer i = new ASN1Integer(rawInt);
            fail("Expecting illegal argument exception.");
        }
        catch (Throwable t)
        {
            isEquals("Expected decoder exception.", IllegalArgumentException.class, t.getClass());
        }
    }

    public void testLooseInvalidValidEncoding_FF_32B()
        throws Exception
    {
        System.setProperty("org.bouncycastle.asn1.allow_unsafe_integer", "false");
        //
        // Should still fail as loose validation only permits 3 leading 0xFF bytes.
        //
        try
        {
            System.getProperties().put("org.bouncycastle.asn1.allow_unsafe_integer", "true");
            byte[] rawInt = Hex.decode("FFFFFFFF10FF");
            ASN1Integer i = new ASN1Integer(rawInt);
            fail("Expecting illegal argument exception.");
        }
        catch (Throwable t)
        {
            isEquals("Expected decoder exception.", IllegalArgumentException.class, t.getClass());
        }
    }

    public void testLooseValidEncoding_zero_32BAligned()
        throws Exception
    {
        System.setProperty("org.bouncycastle.asn1.allow_unsafe_integer", "false");
        //
        // Should pass as loose validation permits 3 leading 0x00 bytes.
        //

        System.getProperties().put("org.bouncycastle.asn1.allow_unsafe_integer", "true");
        byte[] rawInt = Hex.decode("00000010FF000000");
        ASN1Integer i = new ASN1Integer(rawInt);
        isEquals(72997666816L, i.getValue().longValue());

    }

    public void testLooseValidEncoding_FF_32BAligned()
        throws Exception
    {
        System.setProperty("org.bouncycastle.asn1.allow_unsafe_integer", "false");
        //
        // Should pass as loose validation permits 3 leading 0xFF bytes.
        //

        System.getProperties().put("org.bouncycastle.asn1.allow_unsafe_integer", "true");
        byte[] rawInt = Hex.decode("FFFFFF10FF000000");
        ASN1Integer i = new ASN1Integer(rawInt);
        isEquals(-1026513960960L, i.getValue().longValue());

    }

    public void testLooseValidEncoding_FF_32BAligned_1not0()
        throws Exception
    {
        System.setProperty("org.bouncycastle.asn1.allow_unsafe_integer", "false");
        //
        // Should pass as loose validation permits 3 leading 0xFF bytes.
        //

        System.getProperties().put("org.bouncycastle.asn1.allow_unsafe_integer", "true");
        byte[] rawInt = Hex.decode("FFFEFF10FF000000");
        ASN1Integer i = new ASN1Integer(rawInt);
        isEquals(-282501490671616L, i.getValue().longValue());

    }

    public void testLooseValidEncoding_FF_32BAligned_2not0()
        throws Exception
    {
        System.setProperty("org.bouncycastle.asn1.allow_unsafe_integer", "false");
        //
        // Should pass as loose validation permits 3 leading 0xFF bytes.
        //

        System.getProperties().put("org.bouncycastle.asn1.allow_unsafe_integer", "true");
        byte[] rawInt = Hex.decode("FFFFFE10FF000000");
        ASN1Integer i = new ASN1Integer(rawInt);
        isEquals(-2126025588736L, i.getValue().longValue());

    }

    public static void main(
        String[] args)
    {
        runTest(new ASN1IntegerTest());
    }
}