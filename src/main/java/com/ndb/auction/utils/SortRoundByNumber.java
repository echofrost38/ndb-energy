package com.ndb.auction.utils;

import com.ndb.auction.models.Auction;

import org.springframework.stereotype.Component;

@Component
public class SortRoundByNumber {
    private void merge(Auction arr[], int left, int middle, int right)
    {
        int low = middle - left + 1;                    //size of the left subarray
        int high = right - middle;                      //size of the right subarray
 
        Auction L[] = new Auction[low];                             //create the left and right subarray
        Auction R[] = new Auction[high];

        int i = 0, j = 0;
 
        for (i = 0; i < low; i++)                               //copy elements into left subarray
        {
            L[i] = arr[left + i];
        }
        for (j = 0; j < high; j++)                              //copy elements into right subarray
        {
            R[j] = arr[middle + 1 + j];
        }
        

        int k = left;                                           //get starting index for sort
        i = 0;                                             //reset loop variables before performing merge
        j = 0;

        while (i < low && j < high)                     //merge the left and right subarrays
        {
            if (L[i].getNumber() > R[j].getNumber()) 
            {
                arr[k] = L[i];
                i++;
            } 
            else 
            {
                arr[k] = R[j];
                j++;
            }
            k++;
        }
 
        while (i < low)                             //merge the remaining elements from the left subarray
        {
            arr[k] = L[i];
            i++;
            k++;
        }
 
        while (j < high)                           //merge the remaining elements from right subarray
        {
            arr[k] = R[j];
            j++;
            k++;
        }
    }
 
    public void mergeSort(Auction arr[], int left, int right)       //helper function that creates the sub cases for sorting
    {
        int middle;
        if (left < right) {                             //sort only if the left index is lesser than the right index (meaning that sorting is done)
            middle = (left + right) / 2;
 
            mergeSort(arr, left, middle);                    //left subarray
            mergeSort(arr, middle + 1, right);               //right subarray
 
            merge(arr, left, middle, right);                //merge the two subarrays
        }
    }
}
