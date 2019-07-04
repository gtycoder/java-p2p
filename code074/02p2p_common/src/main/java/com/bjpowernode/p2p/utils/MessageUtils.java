package com.bjpowernode.p2p.utils;

public class MessageUtils {
    public static String getMessage(int count) {
        String[] arr={"0","1","2","3","4","5","6","7","8","9"};
        StringBuilder sb=new StringBuilder();

        for (int i = 0; i < count; i++) {
            int index=(int)Math.round(Math.random()*9);
            sb.append(arr[index]);
        }
        return sb.toString();
    }

/*    public static void main(String[] args) {
        String message = getMessage(4);
        System.out.println(message);

    }*/
}
