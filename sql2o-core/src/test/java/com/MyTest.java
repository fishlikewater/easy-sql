package com;

import org.junit.Test;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName MyTest
 * @Description
 * @date 2018年12月29日 22:17
 **/
public class MyTest {

    @Test
    public void test1(){
        int[] arr = {1,4,3,7,2,9,3};
        qucksort(arr, 0, arr.length-1);
    }

    private void quickSort(int[] arr, int low, int high) {
        int i,j,temp,t;
        if(low>high){
            return;
        }
        i=low;
        j=high;
        //temp就是基准位
        temp = arr[low];

        while (i<j) {
            //先看右边，依次往左递减
            while (temp<=arr[j]&&i<j) {
                j--;
            }
            //再看左边，依次往右递增
            while (temp>=arr[i]&&i<j) {
                i++;
            }
            //如果满足条件则交换
            if (i<j) {
                t = arr[j];
                arr[j] = arr[i];
                arr[i] = t;
            }

        }
        //最后将基准为与i和j相等位置的数字交换
        arr[low] = arr[i];
        arr[i] = temp;
        //递归调用左半数组
        quickSort(arr, low, j-1);
        //递归调用右半数组
        quickSort(arr, j+1, high);
    }

    private void qucksort(int[] arr, int low, int len) {
        int temp = arr[low];
        if(low>=len){
            return;
        }
        while (low<len){
            while (low<len && arr[len]>=temp){
                len--;
            }
            arr[low] = arr[len];

            while (low<len && arr[low]<=temp){
                low++;
            }
            arr[len] = arr[low];
        }
        arr[low] = temp;
        qucksort(arr, 0, low-1);
        qucksort(arr, low+1, arr.length-1);
    }

    public static void main(String[] args) {
        B b = new B();
    }
}


class A{

    public final String a = "";

    public static void method1(){

    }

    private  void method2(){

    }
}

class B extends A{


}

