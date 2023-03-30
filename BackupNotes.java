package com.devimpact.inote.settings.changeapptheme;

import java.io.FileWriter;
import java.io.IOException;

public class BackupNotes {
    
    public static void backupNotes(String notes) {
        try {
            FileWriter writer = new FileWriter("notes_backup.txt");
            writer.write(notes);
            writer.close();
            System.out.println("Backup created successfully!");
        } catch (IOException e) {
            System.out.println("An error occurred while creating backup: " + e.getMessage());
        }
    }
    
}
