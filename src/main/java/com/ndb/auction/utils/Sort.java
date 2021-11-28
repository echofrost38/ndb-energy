package com.ndb.auction.utils;

import org.springframework.stereotype.Component;

import com.ndb.auction.models.Bid;

@Component
public class Sort {
    private void merge(Bid arr[], int left, int middle, int right)
    {
        int low = middle - left + 1;                    //size of the left subarray
        int high = right - middle;                      //size of the right subarray
 
        Bid L[] = new Bid[low];                             //create the left and right subarray
        Bid R[] = new Bid[high];

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
            if (L[i].getTokenPrice() > R[j].getTokenPrice()) 
            {
                arr[k] = L[i];
                i++;
            } else if(L[i].getTokenPrice() == R[j].getTokenPrice()) {
            	// equal price
            	if(L[i].getTotalPrice() > R[j].getTotalPrice()) {
            		arr[k] = L[i];
                    i++;
            	} else if (L[i].getTotalPrice() == R[j].getTotalPrice()) {
            		if(L[i].getPlacedAt() <= R[j].getPlacedAt()) {
            			arr[k] = L[i];
                        i++;
            		} else {
            			arr[k] = R[j];
                        j++;	
            		}
            	} else {
            		arr[k] = R[j];
                    j++;
            	}
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
 
    public void mergeSort(Bid arr[], int left, int right)       //helper function that creates the sub cases for sorting
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