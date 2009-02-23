package guineu.data.parser.impl;

public enum Lipidclass_enum {
    unknown (0),
    Cer (1),
    ChoE (2),
    DAG (3),
    GPA (4),
    GPCho (5),
    GPEtn (7),
    GPGro (9),
    GPIns (10),
    GPSer (11),
    LysoGPA (12),
    LysoGPCho (13),
    LysoGPEtn (15),
    LysoGPSer (17),
    LysoGPIns (22),
    LysoGPGro (23),
    MAG (19),
    SM (20),
    TAG (21),
    DG (3),
    PA (4),
    PE (7),
    PC (5),
    PG (9),
    PS (11),
    PI (10),
    LPA (12),
    LPC (13),
    LPE (15),
    LPI (22),
    LPG (23),
    LPS (17),
    MG (19),
    TG (21),
    LysoPA (12),
    LysoPC (13),
    LysoPE (15),
    LysoPI (22),
    LysoPG (23),
    LysoPS (17);
    
	 
    private final int lipid_class;
	
    Lipidclass_enum(int lipid_class) {	
        this.lipid_class = lipid_class;
    }
    
    public int lipid_class(){
    	return lipid_class;
    }
    
}
	