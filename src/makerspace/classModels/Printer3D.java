package makerspace.classModels;

public class Printer3D extends Equipment{
	private String printTech; //FDM, SLA, SLS
	public String getPrintTech() { return printTech; }
	
	private String printVolume;
	public String getPrintVolume() { return printVolume; }
	
	private String printMaterial;
	public String getPrintMaterial() { return printMaterial; }
	public void setPrintMaterial(String printMaterial) { this.printMaterial = printMaterial; }
	
	private double nozzleTemp;
	public double getNozzleTemp() { return nozzleTemp; }
	public void setNozzleTemp(double nozzleTemp) { this.nozzleTemp = nozzleTemp; }
	
	private boolean heatedBed;
	public boolean heatedBed() { return heatedBed; }
	
	
	public Printer3D(String equipmentId, String name, double hourCost, String location, String printTech, String printVolume) 
	{
		super(equipmentId, name, "3D_PRINTER", hourCost, location);
		this.printTech = printTech;
		this.printVolume = printVolume;
		this.nozzleTemp = 200.0; //default range
		this.heatedBed = true;
	}
	
	public boolean canPrintMaterial(String material)
	{
		return printMaterial != null && printMaterial.toLowerCase().contains(material.toLowerCase());
	}
	
	@Override
	public String getEquipmentInfo()
	{
		return String.format("%s | %s 3D Printer (%s) - $%.2f/HR - Max: %s",
				equipmentId,
				name,
				printTech,
				hourCost,
				printVolume);
	}
	
	@Override
	public double calculateRate(int hours) {
		double baseRate = super.calculateRate(hours);
		double materialSurcharge = hours * 2.0; // $2/hr material surcharge
		return baseRate + materialSurcharge;
	}
	
	
}
