import eu.mihosoft.vrl.v3d.parametrics.*;
import com.neuronrobotics.bowlerstudio.vitamins.Vitamins;


CSGDatabase.clear()//set up the database to force only the default values in

LengthParameter thickness 		= new LengthParameter(	"Material Thickness",
												3.2,
												[10,1])
LengthParameter headDiameter 		= new LengthParameter(	"Head Dimeter",
												165,
												[200,140])
LengthParameter snoutLen 		= new LengthParameter("Snout Length",150,[headDiameter.getMM()*2,headDiameter.getMM()/2])
LengthParameter jawHeight 		= new LengthParameter("Jaw Height",32,[200,10])
LengthParameter JawSideWidth 		= new LengthParameter("Jaw Side Width",20,[40,10])
LengthParameter boltDiam 		= new LengthParameter("Bolt Diameter",3.0,[8,2])
LengthParameter boltLength		= new LengthParameter("Bolt Length",10,[18,10])
LengthParameter nutDiam 		 	= new LengthParameter("Nut Diameter",5.42,[10,3])
LengthParameter nutThick 		= new LengthParameter("Nut Thickness",2.4,[10,3])
LengthParameter upperHeadDiam 	= new LengthParameter("Upper Head Height",20,[300,0])
LengthParameter leyeDiam 		= new LengthParameter("Left Eye Diameter",65,[headDiameter.getMM()/2,56])
LengthParameter reyeDiam 		= new LengthParameter("Right Eye Diameter",65,[headDiameter.getMM()/2,56])
LengthParameter eyeCenter 		= new LengthParameter("Eye Center Distance",headDiameter.getMM()/2+thickness.getMM()*2,[headDiameter.getMM(),leyeDiam.getMM()*1.5])
LengthParameter ballJointPin		= new LengthParameter("Ball Joint Pin Size",8,[50,8])
LengthParameter centerOfBall 		= new LengthParameter("Center Of Ball",18.5,[50,8])
LengthParameter printerOffset		= new LengthParameter("printerOffset",0.5,[2,0.001])
LengthParameter eyemechRadius		= new LengthParameter("Eye Mech Linkage",16,[20,5])
LengthParameter eyemechWheelHoleDiam	= new LengthParameter("Eye Mech Wheel Center Hole Diam",7.25,[8,3])
LengthParameter wireDiam			= new LengthParameter("Connection Wire Diameter",1.6,[boltDiam.getMM(),1])
StringParameter servoSizeParam 			= new StringParameter("hobbyServo Default","towerProMG91",Vitamins.listVitaminSizes("hobbyServo"))
StringParameter hornSizeParam 			= new StringParameter("hobbyServoHorn Default","standardMicro1",Vitamins.listVitaminSizes("hobbyServoHorn"))
StringParameter boltSizeParam 			= new StringParameter("Bolt Size","8#32",Vitamins.listVitaminSizes("capScrew"))


def headParts  = (ArrayList<CSG> )ScriptingEngine.gitScriptRun(
	"https://github.com/madhephaestus/ParametricAnimatronics.git", 
	"AnimatronicHead.groovy" ,  
	[false] )
println "Creating cutsheet"
ArrayList<CSG> sheetParts = new ArrayList<>()
for(int i=0;i<headParts.size()-7;i++){
	sheetParts.add(headParts.get(i))
}
ICSGProgress  oldUpdater = CSG.getProgressMoniter()
CSG.setProgressMoniter(new ICSGProgress() {
	public void progressUpdate(int currentIndex, int finalIndex, String type, CSG intermediateShape) {
		println type+" "+currentIndex+" of "+ finalIndex
		BowlerStudioController.addCsg(intermediateShape);
	}
});
def allParts = 	sheetParts.collect { it.prepForManufacturing() } 
CSG cutSheet = CSG.unionAll(allParts)
CSG.setProgressMoniter(oldUpdater);
headParts.add(cutSheet )
return headParts