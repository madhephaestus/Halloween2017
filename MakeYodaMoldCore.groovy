double skinThickness = 5
File yodaFile = ScriptingEngine.fileFromGit(
	"https://github.com/madhephaestus/Halloween2017.git",
	"Yoda-SuperLite.stl");
// Load the .CSG from the disk and cache it in memory
CSG yoda  = Vitamins.get(yodaFile);

println "Total X dimention = " + (-yoda.getMinX()+yoda.getMaxX())

CSG cutter = new Cube(250).toCSG()
				.toZMin()
yoda=yoda .scale(1.6)
		.intersect(cutter
				.movez(20))
		.toZMin()




ArrayList<CSG> moldParts =[]
BowlerStudioController.setCsg([yoda])
def angles = [50,90,58,60,60]

double total =0;
double radius =135
double height = 130*1.75


CSG moldMoldCoreBit =new Cylinder(radius,radius,height,(int)30).toCSG() // a one line Cylinder

for (int i=0;i<angles.size()+1;i++){
	double angle=0;
	if(i<angles.size())
		angle=angles.get(i)
	else{
		angle =360-total
	}

	CSG regestration = new Icosahedron(skinThickness).toCSG()
					.scaley(0.5)

	regestration=regestration
				.movez((skinThickness*2))
				.union(regestration
				.movez(height-(skinThickness*2)))
				.movex(radius-(skinThickness*2))
	
	double x=Math.cos(Math.toRadians(angle))*radius*4
	double y=Math.sin(Math.toRadians(angle))*radius*4
	CSG slice = Extrude.points(new Vector3d(0, 0, height),// This is the  extrusion depth
                new Vector3d(0,0),// All values after this are the points in the polygon
                new Vector3d(x,y),// Bottom right corner
                new Vector3d(radius,0),// upper right corner
        )
        .intersect(moldMoldCoreBit)
        .union(regestration)
        .difference(regestration.rotz(-angle))
        .rotz(-total)
	   .difference(yoda)
      /*
	CSG slice =moldCore.intersect(
				cutter
					.toXMin()
					.toYMin()
					.rotz(i) 
					)
					
				*/
	total+=angle;
	moldParts.add(slice)
	
	String fn = System.getProperty("user.home") + "/Desktop/New_MoldPart_"+i+"_.stl"
	
	File stl = new File(fn)
	if(!stl.exists()){
		stl.createNewFile()
	}
	FileUtil.write(Paths.get(stl.getAbsoluteFile().toString()),
	slice.toStlString());
	BowlerStudioController.setCsg(moldParts)
	println "Making " +fn
}

//CSG printNozzel = new Cube(skinThickness).toCSG();
// Access the raw minkowski intermediates
/*
ArrayList<CSG> mikObjs = yoda.hull().minkowski(printNozzel);
CSG core = yoda
int i=0;
for(CSG bit:mikObjs){
	core = core.difference(bit)
	BowlerStudioController.setCsg([core,bit.setColor(javafx.scene.paint.Color.CYAN)])
	println "Making core part "+i+" of "+mikObjs.size()
	
}
*/

return moldParts