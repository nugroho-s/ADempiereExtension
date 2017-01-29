/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eevolution.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author Nugroho <massatriya@gmail.com>
 */
public class ListShortcut {

    /**
     *
     * @author Nugroho <massatriya@gmail.com>
     */
    public class Shortcut {

        public String nama;
        public String shortcut;
        /**
         * Array berisikan frame yang dapat menggunakan shortcut tersebut
         */
        public ArrayList<String> listFrame;

        public Shortcut(String nama, String shortcut, String[] listFrame) {
            this.nama = nama;
            this.shortcut = shortcut;
            this.listFrame = new ArrayList<String>(Arrays.asList(listFrame));
        }
    }

    /**
     *
     */
    public ArrayList<Shortcut> listShortcut = new ArrayList<Shortcut>(Arrays.asList(
            new Shortcut("product info", "Alt+i", new String[]{"mainframe", "a"}),
            new Shortcut("business partner info", "Ctrl+Alt+i", new String[]{"mainframe", "a"})
            ));

//   public void initialize(){
//       Shortcut Alt_i = new Shortcut("product_info","Alt+i",new String[] {"mainframe","a"});
//       this.listShortcut = new ArrayList<Shortcut>();
//       
//   }
}
