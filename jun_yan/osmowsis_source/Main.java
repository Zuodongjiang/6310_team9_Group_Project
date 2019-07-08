public class Main {

    public static void main(String[] args) {
        Simulation simulation = new Simulation(args[0]);

        // check for the test scenario file name
        if (args.length == 0) {
            System.out.println("ERROR: Test scenario file name not found.");
        } else {
            simulation.readFile(args[0]);
            testControlRunLoop(simulation);
            // testScan(simulation);
            // testMoveMower(simulation);
            // testGenerateSurrounding(simulation, simulation.getLawn());
            // testGenerateReport(simulation);
            // testUpdateRelCoord(simulation.getMower());
            // testUpdatePartialMap()

        }
    }
    public static void testUpdateRelCoord(LawnMower lawnMower) {
        Move move = new Move();
        move.setDirection(MowerStatus.Direction.southwest);
        move.setSteps(2);
        lawnMower.updateRelCoord(move);
    }
    public static void testScan(Simulation simulation) {
        // test scan surrounding
        simulation.scanSurrounding();
    }

    public static void testMoveMower(Simulation simulation) {
        // test scan surrounding
        Move move = new Move();
        move.setDirection(MowerStatus.Direction.east);
        move.setSteps(5);

        simulation.moveMower(move);
    }

    public static void testGenerateSurrounding(Simulation simulation, Lawn lawn) {
        // test scan surrounding
        Coordinate coord = new Coordinate(3, 3);
        simulation.printSurrounding(simulation.generateSurrounding(coord, lawn));
    }

    public static void testGenerateReport(Simulation simulation) {
        simulation.generateReport();
    }

    public static void testControlRunLoop(Simulation simulation) {
        simulation.controlRunLoop();
    }
}