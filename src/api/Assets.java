package api;
import java.awt.image.BufferedImage;

public class Assets {
	
	private static final int width = 120, hight = 120;
	
	public static BufferedImage koenigS, koenigW, dameS, dameW, laeuferS, laeuferW, springerS, springerW, turmS, turmW, bauerS, bauerW;
	public static BufferedImage gruen, rot, rahmen, yellow;
	public static BufferedImage cts, connected, notConnected, secondPlayer, startGame, startOnlineGame;
	public static BufferedImage zero, one, two, three, four, five, six, seven, eight, nine, dp, min, mult;
	public static BufferedImage koenigSC, dameSC, laeuferSC, springerSC, turmSC, bauerSC;
	
	public static BufferedImage bgrWhite 	= ImageLoader.loadImage("/textures/boards/backgroundWhite.png");
	public static BufferedImage bgrBlack 	= ImageLoader.loadImage("/textures/boards/backgroundBlack.png");
	public static BufferedImage bgrCF		= ImageLoader.loadImage("/textures/boards/backgroundChooseFigure.png");
	
	public static BufferedImage whiteWon 	= ImageLoader.loadImage("/textures/boards/whiteWon.png");
	public static BufferedImage blackWon 	= ImageLoader.loadImage("/textures/boards/blackWon.png");
	public static BufferedImage patt 		= ImageLoader.loadImage("/textures/boards/patt.png");
	
	public static BufferedImage sideBarBlanc 	= ImageLoader.loadImage("/textures/sideBars/sideBar.png");
	public static BufferedImage sideBarOnlyFF	= ImageLoader.loadImage("/textures/sideBars/sideBar1.png");
	public static BufferedImage sideBarFFnRedo 	= ImageLoader.loadImage("/textures/sideBars/sideBar2.png");
	public static BufferedImage sideBarReview 	= ImageLoader.loadImage("/textures/sideBars/sideBar3.png");
	public static BufferedImage sideBarRedoHL 	= ImageLoader.loadImage("/textures/sideBars/sideBar4.png");
		
	public static void init() {
		SpriteSheet sheet = new SpriteSheet(ImageLoader.loadImage("/textures/sprites.png"));
		koenigS 	= sheet.crop(0*(width), 1*(hight), width, hight);
		koenigW 	= sheet.crop(0*(width), 0*(hight), width, hight);
		dameS		= sheet.crop(1*(width), 1*(hight), width, hight);
		dameW		= sheet.crop(1*(width), 0*(hight), width, hight);
		laeuferS	= sheet.crop(2*(width), 1*(hight), width, hight);
		laeuferW	= sheet.crop(2*(width), 0*(hight), width, hight);
		springerS 	= sheet.crop(3*(width), 1*(hight), width, hight);
		springerW	= sheet.crop(3*(width), 0*(hight), width, hight);
		turmS		= sheet.crop(4*(width), 1*(hight), width, hight);
		turmW		= sheet.crop(4*(width), 0*(hight), width, hight);
		bauerS 		= sheet.crop(5*(width), 1*(hight), width, hight);
		bauerW		= sheet.crop(5*(width), 0*(hight), width, hight);
		gruen 		= sheet.crop(6*(width), 0*(hight), width, hight);
		rot 		= sheet.crop(7*(width), 0*(hight), width, hight);
		yellow		= sheet.crop(7*(width), 1*(hight), width, hight);
		rahmen 		= sheet.crop(6*(width), 1*(hight), width, hight);
		
		cts 			= sheet.crop(0*(width), 2*(hight), 6*width, hight);
		connected 		= sheet.crop(6*(width), 2*(hight), width, 	hight);
		notConnected	= sheet.crop(7*(width), 2*(hight), width,	hight);
		secondPlayer	= sheet.crop(0*(width), 3*(hight), 6*width, hight);
		startGame		= sheet.crop(0*(width), 4*(hight), 6*width, hight);
		startOnlineGame	= sheet.crop(0*(width), 5*(hight), 6*width, hight);
		
		koenigSC 	= sheet.crop(6*(width), 3*(hight), width, hight);
		dameSC 		= sheet.crop(7*(width), 3*(hight), width, hight);
		laeuferSC	= sheet.crop(6*(width), 4*(hight), width, hight);
		springerSC	= sheet.crop(7*(width), 4*(hight), width, hight);
		turmSC		= sheet.crop(6*(width), 5*(hight), width, hight);
		bauerSC 	= sheet.crop(7*(width), 5*(hight), width, hight);
		
		zero	= sheet.crop(0*(width), 6*(hight), width, hight);
		one		= sheet.crop(1*(width), 6*(hight), width, hight);
		two		= sheet.crop(2*(width), 6*(hight), width, hight);
		three	= sheet.crop(3*(width), 6*(hight), width, hight);
		four	= sheet.crop(4*(width), 6*(hight), width, hight);
		five	= sheet.crop(5*(width), 6*(hight), width, hight);
		six		= sheet.crop(6*(width), 6*(hight), width, hight);
		seven	= sheet.crop(7*(width), 6*(hight), width, hight);
		eight	= sheet.crop(0*(width), 7*(hight), width, hight);
		nine	= sheet.crop(1*(width), 7*(hight), width, hight);
		dp		= sheet.crop(2*(width), 7*(hight), width, hight);
		min		= sheet.crop(3*(width), 7*(hight), width, hight);
		mult	= sheet.crop(4*(width), 7*(hight), width, hight);
		
	}
}