package com.example.asmtest;

/**
 * @Author cwl
 * @Date 2021/12/25 11:54 上午
 * @Description
 */
public class Test {
    void test() {
        //long start = System.currentTimeMillis();
        //long end = System.currentTimeMillis();
        //long dis = end - start;
        //if (dis <= 500) {
        //    StringBuilder sb = new StringBuilder();
        //    sb.append("--> execution time : (");
        //    sb.append(dis);
        //    String s = sb.toString();
        //    System.out.println(s);
        //}
        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        if (time > 500) {
            System.out.println("程序运行时间： " + time + "ms");
        }
    }
}