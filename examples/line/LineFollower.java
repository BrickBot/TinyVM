import tinyvm.rcx.*;

/**
 * @author <a mailto="rvbijl39<at>calvin<dot>edu">Ryan VanderBijl</a>
 */

/* This line follower is based on a robot built from the constructopedia
 * I used the driving base from page 12, using the wheel sets from
 * page 17, and a modification of the light sensor from page 34.
 * For the light sensor, I had the little do hickey stand thing
 * going up, instead of down. This brings the light sensor farther
 * away from the ground, and allowed me to get better readings.
 * (Light sensor still pointing towards the ground. If you couldn't 
 * figure that out, you need help).
 *
 * This program has "some" intelligence. It works with one OR two
 * light sensors attached (I had two sensors).  It automatically
 * detects how many sensors are attached, and modifies its behaivor
 * accordingly. The check_sensor(Sensor s) function is used to 
 * determine if there is a light sensor attached.
 *
 * If there is one sensor attached, it uses that sensor. It doesn't 
 * matter where it is attached. If there are two sensors attached,
 * the one attached to the lowest number is assumed to be on the left.
 * The higher number one is assumed to be on the right. 
 *
 * After downloading, it checks for the sensors, and when done that,
 * it waits for the run button to be pressed.
 *
 * Motors are assumed to be one A (being the motor on the left) 
 * and C (being the motor on the right).
 *
 * You may need to modify the LIGHT and DARK and TIMEOUT settinsg
 * based on your robot and physical settings.
 *
 * This works fairly well on the default test track provided. One
 * sensor robots go clockwise. They can get "lost" because the turn
 * spread brings the sensor to the carpet beside the paper. 
 * For me the carpet is dark enough to be considered a "line".
 *
 * The check_sensor algorithm takes a sampling of readings from each
 * the specified sensor. If the average reading is not really close
 * to zero, a light sensor is assumed to be attached.  (sensors can 
 * give small values w/o anything attached). I believe that other
 * sensors (temperature? rotation?) may give off values like the 
 * light sensor does, and so ... well, its not a fool proof way to
 * detect light sensors.  It works for this.
 *
 * The line_follower algorithm is pretty simple. Maybe i'll also
 * give a subsumption based line follower.
 *
 * This code is very loosely based on the line tracker demo in
 * legOS, by Markus L. Noga.
 */


// all functions are declared static... Not very OO.
public class LineFollower
{
    // i probably used too many "globals".
    private static boolean s1=false, s2=false, s3=false;
    private static int    num_sensors = 0;

    // sensor input
    private static int reading_l, reading_r;
    private static Sensor left  = null;
    private static Sensor right = null;

    private static int dir                 =  0  ;
    private static int counter             =  0  ;

    private static final int POWER    =  7  ;

    private static       int  DARK          =  0  ;
    private static       int  LIGHT         =  0  ;
    private static final int  DARK_1_SENSOR = 36  ;
    private static final int LIGHT_1_SENSOR = 41  ;
    private static final int  DARK_2_SENSOR = 36  ;
    private static final int LIGHT_2_SENSOR = 41  ;

    // 1 and 2 refer to the # of sensors
    // turn_timeout is for turning when we misplaced the line,
    //   not for when we are in locate_line mode. 
    private static final int        TIMEOUT = 150; 
    private static final int TURN_1_TIMEOUT = 150; 
    private static final int TURN_2_TIMEOUT = 150; 
    private static       int   TURN_TIMEOUT =   0; 


    public static void locate_line(boolean had_line)
    {
	// Debug stuff: show a view arrow when in this function
	// LCD.setSegment(Segment.SENSOR_2_VIEW);
	// LCD.refresh();

	// if we only have one light sensor
	if (right == null) reading_r = 0;

	counter = 0;
	if (had_line) { // backtrack for a few seconds.
	    Motor.A.backward();
	    Motor.C.backward();
	    while(counter++ < (2*TIMEOUT)); // do nothing
	}

	// Start motors
	Motor.A.forward();
	Motor.C.forward();

	while(true) {
	    reading_l  = left.readPercentage();
	    if (right != null) reading_r = right.readPercentage();

	    if ((reading_l <= DARK) && (reading_r <= DARK)) {
		// debug: hide the arrow when we leave the function
		// LCD.clearSegment(Segment.SENSOR_2_VIEW);
		// LCD.refresh();
		return;
	    }

	    // turn, and try looking for a line.
	    if (counter++ > TIMEOUT) {
		Motor.A.backward();

		counter = 0;
		while(counter++ < TIMEOUT*3); // wait
		counter = 0;

		Motor.A.forward();
	    }
	}
    }

    public static void follow_line() 
    { 
	int LEFT     =  1000;  // weird values from when i was debugging
	int RIGHT    =  0001;  // I displayed them, the one was which
	int NONE     = -1   ;  // direction it was going
	int STRAIGHT =  0   ;

	dir          =  0   ;
	counter      =  0   ;
	reading_r    =  0   ; // if only one sensor, prettend the right
			      //   is always on the line.

	// make sure we are going forwards.
	Motor.A.forward();
	Motor.C.forward();

	while(true) {
	    dir = STRAIGHT;

	    reading_l =  left.readPercentage();
	    if (right != null) reading_r = right.readPercentage();

	    if      (reading_l >= LIGHT)  dir = RIGHT;
	    else if (reading_r >= LIGHT)  dir = LEFT ;

	    // happens only for two sensors:
	    if ((reading_r >= LIGHT) && (reading_l >= LIGHT)) {
		locate_line(true);
		dir = STRAIGHT;
	    }

	    // one sensor, and we lost the line, try searching left,
	    // then right. if not found, go forward a bit, and search again.
	    if ((right == null) && (dir != STRAIGHT)) {
		if (++counter >    TIMEOUT ) dir = LEFT ;
		if (  counter > (2*TIMEOUT)) dir = RIGHT;
		if (  counter > (3*TIMEOUT)) {
		    locate_line(false);
		    dir = STRAIGHT;
		}
	    } 

	    if (dir == STRAIGHT) {
		// okay then
		counter = 0;
	    } else if (dir == RIGHT) {
		Motor.C.backward();
		for(int k = 0; k < TURN_TIMEOUT; k++);
		Motor.C.forward();
	    } else if (dir == LEFT) {
		Motor.A.backward();
		for(int k = 0; k < TURN_TIMEOUT; k++);
		Motor.A.forward();
	    }
	}
    }




    public static boolean check_sensor(Sensor s)
    {
	final int NUM_TESTS = 50;
	int sum =  0;
	counter =  0;

	// collect samples
	s.activate();
	while(counter++ < NUM_TESTS) {
	    sum += s.readPercentage();
	}
	s.passivate();

	// sensor may give off small numbers w/o sensor attached.
	// so, if we got a non-near zero average, then its good
	if ((sum / NUM_TESTS) > 3) {
	    return true;
	}

	// else, no sensor attached:
	return false;
    }


    public static final void main (String[] arg)
    {
	// constant power
	Motor.A.setPower(POWER);
	Motor.C.setPower(POWER);

	// check each sensor if its a light sensor
	// LCD.setsegments are useless b/c refresh isn't called
	//   anymore, and are in the check_sensor function for
	//   such a short amount of time.

	// LCD.setSegment(Segment.SENSOR_1_VIEW);
	s1 = check_sensor(Sensor.S1); 
	// LCD.clearSegment(Segment.SENSOR_1_VIEW);

	// LCD.setSegment(Segment.SENSOR_2_VIEW);
	s2 = check_sensor(Sensor.S2); 
	// LCD.clearSegment(Segment.SENSOR_2_VIEW);

	// LCD.setSegment(Segment.SENSOR_3_VIEW);
	s3 = check_sensor(Sensor.S3); 
	// LCD.clearSegment(Segment.SENSOR_3_VIEW);

	// count the number of attached sensors
	if (s1) { num_sensors++; }
	if (s2) { num_sensors++; }
	if (s3) { num_sensors++; }

	//if we have 2 light sensors, which ones are connected?
	if (num_sensors == 2) {
	    if (s1       ) { left  = Sensor.S1; }
	    if (s3       ) { right = Sensor.S3; }
	    if (s2 && !s1) { left  = Sensor.S2; }
	    if (s2 &&  s1) { right = Sensor.S2; }
	    if ((left == null) || (right == null))
		return; // error, neither should be null

	    // set the dark/light, set the turn timeout values
	    TURN_TIMEOUT =  TURN_2_TIMEOUT;
	    DARK         =  DARK_2_SENSOR ;
	    LIGHT        = LIGHT_2_SENSOR ;
	} else { 
	    // we have only one, figure out which one it is
	    if (s1)  left = Sensor.S1;
	    if (s2)  left = Sensor.S2;
	    if (s3)  left = Sensor.S3;
	    right = null;

	    // set the dark/light, set the turn timeout values
	    TURN_TIMEOUT =  TURN_1_TIMEOUT;
	    DARK         =  DARK_1_SENSOR ;
	    LIGHT        = LIGHT_1_SENSOR ;
	}

	// show on the LCD which sensors are being used
	if (s1) LCD.setSegment(Segment.SENSOR_1_ACTIVE);
	if (s2) LCD.setSegment(Segment.SENSOR_2_ACTIVE);
	if (s3) LCD.setSegment(Segment.SENSOR_3_ACTIVE);

	// the number of sensors being used?
	LCD.showNumber(num_sensors);
	LCD.refresh();

	if (s1) { LCD.setSegment(Segment.SENSOR_1_ACTIVE); }
	if (s2) { LCD.setSegment(Segment.SENSOR_2_ACTIVE); }
	if (s3) { LCD.setSegment(Segment.SENSOR_3_ACTIVE); }

	left.activate();
	if (right != null) right.activate();

	// do the thing.
	locate_line(false);
	follow_line(     );

	// not that follow_line is ever supposed to return...
	//   but this is just good practice
	left.passivate ();
	if (right != null) right.passivate();

	LCD.clear();
    }
}

