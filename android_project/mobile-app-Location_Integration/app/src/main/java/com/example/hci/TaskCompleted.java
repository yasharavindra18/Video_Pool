package com.example.hci;

import java.util.ArrayList;

public interface TaskCompleted {
    // Define data you like to return from AysncTask
    public void onTaskComplete(ArrayList result);
}