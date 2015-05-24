package DataStructures;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author wernermostert
 */
public class LinkedList {
     class Node{
        Client data;
        Node next;
        public Node(Client o,Node n){
            data = o;
            next = n;
        }
    }
        Node head;
        int count;
    
    public LinkedList(){
        head= null;
        count = 0;
    }    
        
     public boolean isEmpty(){
         return head == null;
     }
     
     public void add(Client data){
         if(head == null)
             head = new Node(data,null);
         else{
             Node cur = head;
             while(cur.next != null) cur = cur.next;
             cur.next = new Node(data,null);
         }
         count++;
     }
     
     public void remove(Client cl){
         Node prev = null;
         Node cur = head;
         while(cur!=null){
             if(cur.data.equals(cl)){
                 if(prev == null) head = null;
                 else prev.next = cur.next;
                 count--;
                 return;
             }
             prev = cur;
             cur = cur.next;
         }
         
     }
     
     public Client[] getAll(){
         Client[] res = new Client[count];
         Node cur = head;
        for(int i = 0; i < count; i++){
            res[i] = cur.data;
            cur = cur.next;
        }
        return res;
     }
     
     public Client getByName(String name){
         Node cur = head;
         while(cur != null){
             if(cur.data.name.equals(name)){
                 return cur.data;
             }
             cur = cur.next;
         }
         return null;
     }
     
     public Client getHead(){
         return head.data;
     }
   
}
