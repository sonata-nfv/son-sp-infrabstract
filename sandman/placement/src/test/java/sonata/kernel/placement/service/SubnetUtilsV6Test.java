package sonata.kernel.placement.service;

import org.junit.Test;

import static org.junit.Assert.*;

public class SubnetUtilsV6Test {
    // TODO: create more meaningful tests
    @Test
    public void createSubnetCidr() throws Exception {
        SubnetUtilsV6 sub = SubnetUtilsV6.createSubnet("2001:0db8:0000:08d3:0000:8a2e:0070:7344/75");
        System.out.println(sub.getInfo().toString());
        String[] addresses = sub.getInfo().getAllAddresses(77);
        for(String a: addresses)
            System.out.println(a);
    }

    @Test
    public void createSubnetAddressAndNetmask() throws Exception {
        SubnetUtilsV6 sub = SubnetUtilsV6.createSubnet("2001:0db8:0000:08d3:0000:8a2e:0070:7344",
                "ffff:ffff:ffff:ffff:ffff:ffff::");
        System.out.println(sub.getInfo().toString());
    }

    @Test
    public void isInclusiveHostCount() throws Exception {

    }

    @Test
    public void setInclusiveHostCount() throws Exception {

    }

    @Test
    public void getInfo() throws Exception {
        SubnetUtilsV6 sub = SubnetUtilsV6.createSubnet("2001:0db8:0000:08d3:0000:8a2e:0070:7344",
                "ffff:ffff:ffff:ffff:ffff:ffff::");
        System.out.println(sub.getInfo().toString());
    }

}