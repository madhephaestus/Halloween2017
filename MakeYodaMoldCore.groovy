File yodaFile = ScriptingEngine.fileFromGit(
	"https://github.com/madhephaestus/Halloween2017.git",
	"Yoda-SuperLite.stl");
// Load the .CSG from the disk and cache it in memory
CSG yoda  = Vitamins.get(yodaFile);
double scale = 1.91
println "Total X dimention = " + (-yoda.getMinX()+yoda.getMaxX())

CSG cutter = new Cube(250).toCSG()
				.toZMin()
yoda=yoda.scale(scale)
		.intersect(cutter
				.movez(20))
		.toZMin()

CSG moldCore =new Cylinder(100,100,130,(int)30).toCSG() // a one line Cylinder
			.difference(yoda)

ArrayList<CSG> moldParts =[]

for (int i=0;i<360;i+=90){
	CSG slice =moldCore.intersect(
				cutter
					.toXMin()
					.toYMin()
					.rotz(i) 
					)
	moldParts.add(slice)
	String fn = "C:\\Users\\Technocopia PC 01\\Desktop\\MoldPart_"+i+"_.stl"
	File stl = new File(fn)
	if(!stl.exists()){
		stl.createNewFile()
	}
	FileUtil.write(Paths.get(stl.getAbsoluteFile().toString()),
	slice.toStlString());
	println "Making " +fn
}




return moldParts