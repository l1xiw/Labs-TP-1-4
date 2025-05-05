package Maps;

import Buildings.Warrior;

public class Cell {
    private String Celltype;
    //private Warrior warrior;

    public Cell(String Celltype){
        this.Celltype = Celltype;
        //this.warrior = null;
    }
    public void setCelltype(String celltype){
        this.Celltype = celltype;
    }
    public String getCelltype(){
        return Celltype;
    }
    /*public Warrior setWarrior(Warrior warrior){
        this.warrior = warrior;
        return warrior;
    }
    public Warrior getWarrior(){
        return warrior;
    }*/
}