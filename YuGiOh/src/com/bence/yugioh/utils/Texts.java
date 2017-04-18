package com.bence.yugioh.utils;

public class Texts {
	public static String NextPhaseText;
	public static String NewGameText;
	public static String LoadGameText;
	public static String ExitGameText;
	public static String SaveGameText;
	public static String YouLostText;
	public static String YouWinText;
	public static String HealthText;
	public static String ComputerText;
	public static String PlayerText;
	public static String YourRoundText;
	public static String EnemyRoundText;
	public static String PhaseText;
	public static String DefenseModeText;
	public static String AttackModeText;
	public static String SpecialText;
	public static String AddATKAllText;
	public static String AddATKEffectText;
	public static String AddDEFAllText;
	public static String AddDEFEffectText;
	public static String DestroyCardText;
	public static String GrabRandomCardText;
	public static String HealPlayerText;
	public static String AttackPhaseNameText;
	public static String CardPickPhaseText;
	public static String TacticsPhaseText;
	
	public static void Init() throws Exception{
		TacticsPhaseText = new String(new byte[]{(byte)84,(byte)97,(byte)107,(byte)116,(byte)105,(byte)107,(byte)97,(byte)105}, "UTF8");
		CardPickPhaseText = new String(new byte[]{(byte)75,(byte)195,(byte)161,(byte)114,(byte)116,(byte)121,(byte)97,(byte)32,(byte)104,(byte)195,(byte)186,(byte)122,(byte)195,(byte)161,(byte)115,(byte)46}, "UTF8");
		AttackPhaseNameText = new String(new byte[]{(byte)84,(byte)195,(byte)161,(byte)109,(byte)97,(byte)100,(byte)195,(byte)179,(byte)32,(byte)102,(byte)195,(byte)161,(byte)122,(byte)105,(byte)115,(byte)46}, "UTF8");
		HealPlayerText = new String(new byte[]{(byte)65,(byte)32,(byte)107,(byte)195,(byte)161,(byte)114,(byte)116,(byte)121,(byte)97,(byte)32,(byte)104,(byte)97,(byte)115,(byte)122,(byte)110,(byte)195,(byte)161,(byte)108,(byte)195,(byte)179,(byte)106,(byte)97,(byte)32,(byte)107,(byte)97,(byte)112,(byte)32,(byte)123,(byte)48,(byte)125,(byte)32,(byte)195,(byte)169,(byte)108,(byte)101,(byte)116,(byte)101,(byte)114,(byte)197,(byte)145,(byte)32,(byte)112,(byte)111,(byte)110,(byte)116,(byte)111,(byte)116,(byte)46}, "UTF8");
		GrabRandomCardText = new String(new byte[]{(byte)65,(byte)122,(byte)32,(byte)101,(byte)108,(byte)108,(byte)101,(byte)110,(byte)102,(byte)195,(byte)169,(byte)108,(byte)32,(byte)107,(byte)101,(byte)122,(byte)195,(byte)169,(byte)98,(byte)197,(byte)145,(byte)108,(byte)32,(byte)107,(byte)97,(byte)112,(byte)101,(byte)115,(byte)122,(byte)32,(byte)101,(byte)103,(byte)121,(byte)32,(byte)118,(byte)195,(byte)169,(byte)108,(byte)101,(byte)116,(byte)108,(byte)101,(byte)110,(byte)115,(byte)122,(byte)101,(byte)114,(byte)197,(byte)177,(byte)32,(byte)107,(byte)195,(byte)161,(byte)114,(byte)116,(byte)121,(byte)195,(byte)161,(byte)116,(byte)46,(byte)32,(byte)65,(byte)32,(byte)107,(byte)195,(byte)161,(byte)114,(byte)116,(byte)121,(byte)97,(byte)32,(byte)108,(byte)101,(byte)104,(byte)101,(byte)108,(byte)121,(byte)101,(byte)122,(byte)195,(byte)169,(byte)115,(byte)101,(byte)107,(byte)111,(byte)114,(byte)32,(byte)97,(byte)107,(byte)116,(byte)105,(byte)118,(byte)195,(byte)161,(byte)108,(byte)195,(byte)179,(byte)100,(byte)105,(byte)107,(byte)46}, "UTF8");
		DestroyCardText = new String(new byte[]{(byte)65,(byte)122,(byte)32,(byte)101,(byte)108,(byte)108,(byte)101,(byte)110,(byte)102,(byte)195,(byte)169,(byte)108,(byte)32,(byte)101,(byte)103,(byte)121,(byte)32,(byte)118,(byte)195,(byte)169,(byte)108,(byte)101,(byte)116,(byte)108,(byte)101,(byte)110,(byte)115,(byte)122,(byte)101,(byte)114,(byte)197,(byte)177,(byte)32,(byte)107,(byte)195,(byte)161,(byte)114,(byte)116,(byte)121,(byte)195,(byte)161,(byte)106,(byte)97,(byte)32,(byte)109,(byte)101,(byte)103,(byte)115,(byte)101,(byte)109,(byte)109,(byte)105,(byte)115,(byte)195,(byte)188,(byte)108,(byte)46,(byte)32,(byte)65,(byte)32,(byte)107,(byte)195,(byte)161,(byte)114,(byte)116,(byte)121,(byte)97,(byte)32,(byte)108,(byte)101,(byte)104,(byte)101,(byte)108,(byte)121,(byte)101,(byte)122,(byte)195,(byte)169,(byte)115,(byte)101,(byte)107,(byte)111,(byte)114,(byte)32,(byte)97,(byte)107,(byte)116,(byte)105,(byte)118,(byte)195,(byte)161,(byte)108,(byte)195,(byte)179,(byte)100,(byte)105,(byte)107,(byte)46}, "UTF8");
		AddDEFEffectText = new String(new byte[]{(byte)69,(byte)103,(byte)121,(byte)32,(byte)107,(byte)105,(byte)118,(byte)195,(byte)161,(byte)108,(byte)97,(byte)115,(byte)122,(byte)116,(byte)111,(byte)116,(byte)116,(byte)32,(byte)115,(byte)122,(byte)195,(byte)182,(byte)114,(byte)110,(byte)121,(byte)110,(byte)101,(byte)107,(byte)32,(byte)97,(byte)100,(byte)32,(byte)123,(byte)48,(byte)125,(byte)32,(byte)118,(byte)195,(byte)169,(byte)100,(byte)101,(byte)107,(byte)101,(byte)122,(byte)195,(byte)169,(byte)115,(byte)105,(byte)32,(byte)101,(byte)114,(byte)197,(byte)145,(byte)116,(byte)46}, "UTF8");
		AddDEFAllText = new String(new byte[]{(byte)77,(byte)105,(byte)110,(byte)100,(byte)101,(byte)110,(byte)32,(byte)97,(byte)32,(byte)112,(byte)195,(byte)161,(byte)108,(byte)121,(byte)195,(byte)161,(byte)110,(byte)32,(byte)108,(byte)195,(byte)169,(byte)118,(byte)197,(byte)145,(byte)32,(byte)115,(byte)122,(byte)195,(byte)182,(byte)114,(byte)110,(byte)121,(byte)110,(byte)101,(byte)107,(byte)32,(byte)97,(byte)100,(byte)32,(byte)123,(byte)48,(byte)125,(byte)32,(byte)118,(byte)195,(byte)169,(byte)100,(byte)101,(byte)107,(byte)101,(byte)122,(byte)195,(byte)169,(byte)115,(byte)105,(byte)32,(byte)101,(byte)114,(byte)197,(byte)145,(byte)116,(byte)46}, "UTF8");
		AddATKEffectText = new String(new byte[]{(byte)69,(byte)103,(byte)121,(byte)32,(byte)107,(byte)105,(byte)118,(byte)195,(byte)161,(byte)108,(byte)97,(byte)115,(byte)122,(byte)116,(byte)111,(byte)116,(byte)116,(byte)32,(byte)115,(byte)122,(byte)195,(byte)182,(byte)114,(byte)110,(byte)121,(byte)110,(byte)101,(byte)107,(byte)32,(byte)97,(byte)100,(byte)32,(byte)123,(byte)48,(byte)125,(byte)32,(byte)116,(byte)195,(byte)161,(byte)109,(byte)97,(byte)100,(byte)195,(byte)161,(byte)115,(byte)105,(byte)32,(byte)101,(byte)114,(byte)197,(byte)145,(byte)116,(byte)46}, "UTF8");
		AddATKAllText = new String(new byte[]{(byte)77,(byte)105,(byte)110,(byte)100,(byte)101,(byte)110,(byte)32,(byte)97,(byte)32,(byte)112,(byte)195,(byte)161,(byte)108,(byte)121,(byte)195,(byte)161,(byte)110,(byte)32,(byte)108,(byte)195,(byte)169,(byte)118,(byte)197,(byte)145,(byte)32,(byte)115,(byte)122,(byte)195,(byte)182,(byte)114,(byte)110,(byte)121,(byte)110,(byte)101,(byte)107,(byte)32,(byte)97,(byte)100,(byte)32,(byte)123,(byte)48,(byte)125,(byte)32,(byte)116,(byte)195,(byte)161,(byte)109,(byte)97,(byte)100,(byte)195,(byte)161,(byte)115,(byte)105,(byte)32,(byte)101,(byte)114,(byte)197,(byte)145,(byte)116,(byte)46}, "UTF8");
		SpecialText = new String(new byte[]{(byte)83,(byte)112,(byte)101,(byte)99,(byte)105,(byte)195,(byte)161,(byte)108,(byte)105,(byte)115,(byte)32,(byte)107,(byte)195,(byte)169,(byte)112,(byte)101,(byte)115,(byte)115,(byte)195,(byte)169,(byte)103,(byte)58}, "UTF8");
		AttackModeText = new String(new byte[]{(byte)84,(byte)195,(byte)161,(byte)109,(byte)97,(byte)100,(byte)195,(byte)179}, "UTF8");
		DefenseModeText = new String(new byte[]{(byte)86,(byte)195,(byte)169,(byte)100,(byte)101,(byte)107,(byte)101,(byte)122,(byte)197,(byte)145}, "UTF8");
		NextPhaseText = new String(new byte[] { (byte)75, (byte)195, (byte)182, (byte)118, (byte)101, (byte)116, (byte)107, (byte)101, (byte)122, (byte)197, (byte)145 }, "UTF8");
		NewGameText = new String(new byte[] { (byte)195, (byte)154, (byte)106, (byte)32, (byte)106, (byte)195, (byte)161, (byte)116, (byte)195, (byte)169, (byte)107 }, "UTF8");
		LoadGameText = new String(new byte[]{(byte)74,(byte)195,(byte)161,(byte)116,(byte)195,(byte)169,(byte)107,(byte)32,(byte)98,(byte)101,(byte)116,(byte)195,(byte)182,(byte)108,(byte)116,(byte)195,(byte)169,(byte)115,(byte)101}, "UTF8");
		ExitGameText = new String(new byte[]{(byte)75,(byte)105,(byte)108,(byte)195,(byte)169,(byte)112,(byte)195,(byte)169,(byte)115}, "UTF8");
		SaveGameText = new String(new byte[]{(byte)74,(byte)195,(byte)161,(byte)116,(byte)195,(byte)169,(byte)107,(byte)32,(byte)109,(byte)101,(byte)110,(byte)116,(byte)195,(byte)169,(byte)115,(byte)101}, "UTF8");
		YouLostText = new String(new byte[]{(byte)86,(byte)101,(byte)115,(byte)122,(byte)116,(byte)101,(byte)116,(byte)116,(byte)195,(byte)169,(byte)108,(byte)33}, "UTF8");
		YouWinText = new String(new byte[]{(byte)78,(byte)121,(byte)101,(byte)114,(byte)116,(byte)195,(byte)169,(byte)108}, "UTF8");
		HealthText = new String(new byte[]{(byte)195,(byte)137,(byte)108,(byte)101,(byte)116,(byte)101,(byte)114,(byte)197,(byte)145,(byte)58}, "UTF8");
		ComputerText = new String(new byte[]{(byte)83,(byte)122,(byte)195,(byte)161,(byte)109,(byte)195,(byte)173,(byte)116,(byte)195,(byte)179,(byte)103,(byte)195,(byte)169,(byte)112}, "UTF8");
		PlayerText = new String(new byte[]{(byte)74,(byte)195,(byte)161,(byte)116,(byte)195,(byte)169,(byte)107,(byte)111,(byte)115}, "UTF8");
		YourRoundText = new String(new byte[]{(byte)65,(byte)32,(byte)116,(byte)101,(byte)32,(byte)107,(byte)195,(byte)182,(byte)114,(byte)195,(byte)182,(byte)100}, "UTF8");
		EnemyRoundText = new String(new byte[]{(byte)65,(byte)122,(byte)32,(byte)101,(byte)108,(byte)108,(byte)101,(byte)110,(byte)102,(byte)195,(byte)169,(byte)108,(byte)32,(byte)107,(byte)195,(byte)182,(byte)114,(byte)101}, "UTF8");
		PhaseText = new String(new byte[]{(byte)70,(byte)195,(byte)161,(byte)122,(byte)105,(byte)115,(byte)58}, "UTF8");
	}
}
