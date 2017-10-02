package pkg;

import domain.CampusName;

public class Test{
    Test(){
        System.out.println("hi");
    }

    public static void main(String args[]){
        Test test = new Test();
        test.enumValueOfTest();
    }

    void enumValueOfTest(){
        System.out.println(CampusName.getCampusName("DVL"));
    }
}

