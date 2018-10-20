if (args==null){
	CSGDatabase.clear()
}
public ArrayList<CSG> minkowski(CSG core, CSG travelingShape){
	def allParts = []
	for(Polygon p: core.getPolygons()){
		def corners = []
		for(Vertex v:p.vertices){
				corners.add(travelingShape.move(v));
		}
		allParts.add(CSG.hullAll(corners))
	}
	return  allParts;
}
def eyeDiam 		= new LengthParameter("Eye Diameter",50,[60,38])
def eyeCenter 		= new LengthParameter("Eye Center Distance",eyeDiam.getMM()+5,[100,eyeDiam.getMM()])
def noseLength		= new LengthParameter("noseLength",5,[200,001])
def jawLength		= new LengthParameter("jawLength",70,[200,001])
def noseDiameter 	= new LengthParameter("Nose Diameter",eyeDiam.getMM()*2,[eyeDiam.getMM()*3,10])
double jawThickness = 6 

double moveDownYoda = 45
double skinThickness = 5
File yodaFile = ScriptingEngine.fileFromGit(
	"https://github.com/madhephaestus/Halloween2017.git",
	"yoda-1kfaces.stl");
// Load the .CSG from the disk and cache it in memory
CSG yoda  = Vitamins.get(yodaFile)
			.rotz(34)


CSG cutter = new Cube(250).toCSG()
				.toZMin()
yoda=yoda .scale(1.6)
		.intersect(cutter
				.movez(20))
		.toZMin()
		.movez(-moveDownYoda)
		.movey(35)
		 .scale(2.7)
		 .movey(-5)

println "Total X dimention = " + (-yoda.getMinX()+yoda.getMaxX())
		
CSG eye = new Sphere(eyeDiam.getMM()/2).toCSG()
CSG jawBlank = new Cylinder(noseDiameter.getMM()/2,jawThickness).toCSG()	
			.movez(-jawLength.getMM())
			.movey(eyeCenter.getMM()/2)
			.toXMax()
			.movex(noseLength.getMM()+eyeDiam.getMM()/3)
def mechBody = new Cube(noseDiameter.getMM()-eyeDiam.getMM()/3,
				eyeCenter.getMM()+eyeDiam.getMM()-20,
				jawLength.getMM()+eyeDiam.getMM()/2)
				.toCSG()
				.toXMax()
				.toYMin()
				.toZMin()
				.movez(-jawLength.getMM())
				.movey(-eyeDiam.getMM()/2+10)
				//.movex(-jawThickness)
			
def core = CSG.unionAll([
		eye,
		eye.movey(eyeCenter.getMM()),
		jawBlank,
		mechBody
])
.rotz(90)
.movex(-eyeCenter.getMM()/2)

def box = yoda.getBoundingBox()

yoda=yoda.difference(box.toXMin())

return [yoda,core]


ArrayList<CSG> moldParts =[]
BowlerStudioController.setCsg([yoda])
double ear =35
def angles = [90,64,ear,360-(90+64+ear)-ear]

double total =0;
double radius =130
double height = 130*1.75


CSG moldMoldCoreBit =new Cube(radius*2,radius*2,height).toCSG().toZMin() // a one line Cylinder

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
	
	double x=Math.cos(Math.toRadians(angle))*radius*4*Math.sqrt(2)
	double y=Math.sin(Math.toRadians(angle))*radius*4*Math.sqrt(2)
	CSG slice = Extrude.points(new Vector3d(0, 0, height),// This is the  extrusion depth
                new Vector3d(0,0),// All values after this are the points in the polygon
                new Vector3d(x,y),// Bottom right corner
                new Vector3d(Math.cos(Math.toRadians(2*angle/3))*radius*4*Math.sqrt(2),
                Math.sin(Math.toRadians(2*angle/3))*radius*4*Math.sqrt(2)),// Bottom right corner
                new Vector3d(Math.cos(Math.toRadians(1*angle/3))*radius*4*Math.sqrt(2),
                Math.sin(Math.toRadians(1*angle/3))*radius*4*Math.sqrt(2)),// Bottom right corner
                new Vector3d(radius,0),// upper right corner
        )
        .union(regestration)
        .difference(regestration.rotz(-angle))
        .rotz(-total)
	   .difference(yoda)
        .intersect(moldMoldCoreBit)
	   .rotz(total)
	   .movey(5)
	   .movex(5)
	   .rotz(-total)
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
		FileUtil.write(Paths.get(stl.getAbsoluteFile().toString()),
		slice.toStlString());
		BowlerStudioController.setCsg(moldParts)
		println "Making " +fn
	}

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