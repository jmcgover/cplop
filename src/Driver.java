import java.util.*;
import java.io.*;

public class Driver{

    public enum Method {
        MEAN, WINNER, SETWISE, INTERSECTION
    }

    public static void main(String[] args) {
        java.util.Date date = new java.util.Date();
        Phylogeny tree = null;
        String treeFilename = "";
        String resultFilename = "set-approach" +  date.toString().replace(" ","-") + ".res";
        boolean filenameSet = false;
        Winner experiment = null;
        TreeFilter filter;
        Method method = null;
        boolean statistics = false;
        String[] unknownIsolateIDs = null;

        // Experiment Parameters
        // int k[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,17};
        int k[] = {1, 2, 3, 4, 5, 6, 7, 8};
        //double alpha[] = {0.0, .5, .9, .95, .96, .97, .98, .99, 1.0};
        double alpha[] = {0.0, .5, .9, .95, .98, .99};

        /*Argument Parsing*/
        if (args.length > 0) {
            try {
                for (int i = 0; i < args.length; i++) {
                    // Filename
                    if (args[i].equals("-f") | args[i].equals("--filename")) {
                        filenameSet = true;
                        treeFilename = args[++i];
                    }
                    // Output
                    if (args[i].equals("-o") | args[i].equals("--output")) {
                        resultFilename = args[++i];
                        System.out.printf("Using custom results filename %s.\n", resultFilename);
                    }
                    if (args[i].equals("-m") | args[i].equals("--meanwise")) {
                        method = Method.MEAN;
                    }
                    if (args[i].equals("-w") | args[i].equals("--winner")) {
                        method = Method.WINNER;
                    }
                    if (args[i].equals("-s") | args[i].equals("--setwise")) {
                        method = Method.SETWISE;
                    }
                    if (args[i].equals("-i") | args[i].equals("--intersection")) {
                        method = Method.INTERSECTION;
                    }
                    if (args[i].equals("-z") | args[i].equals("--statistics")) {
                        statistics = true;
                    }
                    if (args[i].equals("-c") | args[i].equals("--classify")) {
                        System.out.println("Gonna Test: " + args[i + 1]);
                        unknownIsolateIDs = args[i + 1].split(",");
                    }
                    // Help
                    if (args[i].equals("-h") | args[i].equals("--help")) {
                        printUsage();
                    }
                }
            }
            catch (IndexOutOfBoundsException e) {
                System.err.println(args[args.length - 1] + " needs an argument.");
                System.exit(1);
            }
        }

        if (filenameSet == false) {
            printUsage("Please provide a filename.");
        }
        if (method == null && statistics == false) {
            printUsage("Please specify which method you want to perform.");
        }
        PrintStream resultOut = System.err;
        try {
        if (!statistics) {
            System.err.printf("Using method ");
            switch (method) {
                case MEAN:           System.err.printf("MEAN\n");
                                     resultOut = new PrintStream("avila_mean.json");
                                     break;
                case WINNER:         System.err.printf("WINNER\n");
                                     resultOut = new PrintStream("avila_winner.json");
                                     break;
                case SETWISE:        System.err.printf("SETWISE\n");
                                     resultOut = new PrintStream("avila_union.json");
                                     break;
                case INTERSECTION:   System.err.printf("INTERSECTION\n");
                                     resultOut = new PrintStream("avila_intersection.json");
                                     break;
                default:             System.err.printf("INVALID METHOD %s!\n", method);
                                     printUsage("Please proved a valid method.");
            }
        }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Load Tree
        System.err.println("Loading tree from '" + treeFilename + "'...");
        tree = Phylogeny.load(treeFilename);
        System.err.println("Loaded.");


        // Filter Tree
        System.err.println("Filtering...");
        filter = new TreeFilter();
        tree = filter.removeEnvironmental(tree);
        tree = filter.removeBad(tree);
        tree = filter.removeIncompleteIsolates(tree);
        tree = filter.removeSpeciesBelow(tree, 4);

        LinkedList<Species> allSpecies = new LinkedList<Species>(tree.getAllSpecies().values());
        Collections.sort(allSpecies);

        if (statistics) {
            printMergedStats(System.out, tree);
            System.exit(0);
        }

        //Run Experiments
        Classifier<Isolate, Phylogeny, Species> classifier = null;

        /*Track Results*/
        ExperimentResult[][] results = new ExperimentResult[k.length][alpha.length];
        for (int j = 0; j < k.length; j++) {
            for (int a = 0; a < alpha.length; a++) {
                results[j][a] = new ExperimentResult(k[j], alpha[a], tree);
            }
        }

        if (null != unknownIsolateIDs) {
            List<Isolate> unknownIsolates = new ArrayList<Isolate>(unknownIsolateIDs.length);
            Set<String> unknownIsolateIDSet = new HashSet<String>(unknownIsolateIDs.length);
            for (String id : unknownIsolateIDs) {
                unknownIsolateIDSet.add(id);
            }

            // Build Tree
            String url  = "jdbc:mysql://localhost/CPLOP";
            String user = "root";
            String pass = "Jeffrey";
            System.err.printf("Connecting to %s...\n",url);
            Database database = new Database(url, user, pass);
            System.err.println("Successful connection.");
            TreeBuilder builder = new TreeBuilder(database);
            Phylogeny isolateTree = builder.build(true, unknownIsolateIDSet);

            Set<String> retrievedIDs = new HashSet<String>();
            for (Isolate i : isolateTree.getAllIsolates().values()) {
                System.out.printf("Isolate: %s\n", i.getIsoId());
                retrievedIDs.add(i.getIsoId());
            }
            for (String id : unknownIsolateIDs) {
                if (retrievedIDs.contains(id)) {
                    System.out.printf("Retrieved %s!\n", id);
                } else {
                    System.out.printf("DID NOT RETRIEVE %s!\n", id);
                }
            }

            Species result = null;
            resultOut.printf("{\"results\" : [\n");
            boolean isFirst = true;
            for (Isolate i : isolateTree.getAllIsolates().values()) {
                switch (method) {
                    case MEAN:           classifier = new Meanwise(i, tree);
                                         break;
                    case WINNER:         classifier = new Winner(i, tree);
                                         break;
                    case SETWISE:        classifier = new Setwise(i, tree);
                                         break;
                    case INTERSECTION:   classifier = new Intersection(i, tree);
                                         break;
                    default:             printUsage(String.format("Invalid method: %d", method));
                }
                //resultOut.printf("k,alpha,isoId,classification\n");
                for (int a = 0; a < alpha.length; a++) {
                    for (int j = 0; j < k.length; j++) {
                        result = null;
                        result = classifier.classify(k[j], alpha[a]);
                        //resultOut.printf("%d,%.3f,%s,%s\n", k[j], alpha[a], i, result);
                        if (isFirst) {
                            isFirst = false;
                        } else {
                            resultOut.printf(",\n");
                        }
                        resultOut.printf("{\"k\":%d,\"alpha\" : %.3f, \"isoId\" : \"%s\", \"classification\":\"%s\"}", k[j], alpha[a], i, result);
                    }
                }
            }
            resultOut.printf("]}\n");
        } else {
            Species result = null;
            for (Species s : allSpecies) {
                System.err.println("Testing " + s + "...");
                for (Host h : s.getHosts().values()) {
                    for (Isolate i : h.getIsolates().values()) {
                        switch (method) {
                            case MEAN:           classifier = new Meanwise(i, tree);
                                                 break;
                            case WINNER:         classifier = new Winner(i, tree);
                                                 break;
                            case SETWISE:        classifier = new Setwise(i, tree);
                                                 break;
                            case INTERSECTION:   classifier = new Intersection(i, tree);
                                                 break;
                            default:             printUsage(String.format("Invalid method: %d", method));
                        }
                        for (int a = 0; a < alpha.length; a++) {
                            for (int j = 0; j < k.length; j++) {
                                result = null;
                                result = classifier.classify(k[j], alpha[a]);
                                //                     System.out.printf("CLASSIFIED %s as %s\n", s, result);
                                results[j][a].addClassification(s, result);
                            }
                        }
                    }
                }
            }
        }

            // Save Experiment Results
            System.out.printf("Saving results to %s...\n", resultFilename);
            ExperimentResult.saveArray(results, resultFilename);
            System.out.println("Done.");
        }

        public static ExperimentResult[][] runTests(int[] k, double[] alpha, Phylogeny tree) {
            return null;
        }

        public static void printUsage(String msg){
            System.err.println(msg);
            printUsage();
        }
        public static void printUsage(){
            String usage = "usage: ";
            usage += "java";
            usage += " ";
            usage += Driver.class.getName();
            usage += " ";
            usage += "<options...>";
            System.err.println(usage);
            System.err.println();
            System.err.println("Options:");
            printOption("[-h  | --help]","\tdisplays this help and exits");
            printOption("<-f  | --filename>","filename of the tree to load");
            printOption("[-o  | --output]","filename to save the experiment results as");
            printOption("<method>","\tmethod of classification to use");
            System.err.println("Methods:");
            printOption("[-m  | --meanwise]","mean-based method");
            printOption("[-w  | --winner]","winner-based method");
            printOption("[-s  | --setwise]","set-based method");
            printOption("[-i  | --intersection]","intersection-based method");
            System.exit(1);
        }
        public static void printOption(String flag, String explanation){
            System.err.printf("\t%s\t%s\n",flag,explanation);
        }
        public static void printMergedStats(PrintStream statsStream, Phylogeny tree) {
            LinkedList<Species> allSpecies = new LinkedList<Species>(tree.getAllSpecies().values());
            Collections.sort(allSpecies);

            HashMap<String, String> ignoreList = new HashMap<String, String>();
            ignoreList.put("Cw", "Cw");
            ignoreList.put("Dg", "Dg");
            ignoreList.put("Hu", "Hu");
            ignoreList.put("Cw and Dg", "Cw and Dg");
            ignoreList.put("Hu Cw and Dg", "Hu Cw and Dg");
            ignoreList.put("Hu and Dg", "Hu and Dg");
            ignoreList.put("Hu and Cw", "Hu and Cw");
            ignoreList.put("Human UTI", "Human UTI");
            ignoreList.put("Pig/Swine", "Pig/Swine");
            ignoreList.put("Wild Pig", "Wild Pig");
            ignoreList.put("unknown", "unknown");

            statsStream.printf("%s,%s,%s,%s,%s\n","Index","Species Name","$\\host{}$","$\\isol{}$","$\\pyro{}$");
            int specNum = 0;
            int perSpecHosts = 0;
            int perSpecIsols = 0;
            int perSpecPyros = 0;
            for (Species s : allSpecies) {
                if (null == ignoreList.get(s.getCommonName())) {
                    perSpecHosts = tree.getHostCount(s);
                    perSpecIsols = tree.getIsolateCount(s);
                    perSpecPyros = tree.getPyroprintCount(s);
                    statsStream.printf("%d,%s,%d,%d,%d\n",specNum,s,perSpecHosts,perSpecIsols,perSpecPyros);
                    specNum++;
                }
            }
            int totSpecs = tree.getSpeciesCount();
            int totHosts = tree.getHostCount();
            int totIsols = tree.getIsolateCount();
            int totPyros = tree.getPyroprintCount();
            statsStream.printf("%s,%d,%d,%d,%d\n","Total",specNum - 1,totHosts,totIsols,totPyros);
        }
        public static void printRawStats(PrintStream statsStream, Phylogeny tree) {
            LinkedList<Species> allSpecies = new LinkedList<Species>(tree.getAllSpecies().values());
            Collections.sort(allSpecies);
            System.err.println("Running statistics. Don't give a shit about your experiments.");
            statsStream.printf("%s,%s,%s,%s,%s\n","Index","Species Name","$\\host{}$","$\\isol{}$","$\\pyro{}$");
            int specNum = 1;
            int totSpecs = 0;
            int totHosts = 0;
            int totIsols = 0;
            int totPyros = 0;
            int perSpecHosts = 0;
            int perSpecIsols = 0;
            int perSpecPyros = 0;
            for (Species s : allSpecies) {
                for (Host h : s.getHosts().values()) {
                    for (Isolate i : h.getIsolates().values()) {
                        for (Pyroprint p : i.getPyroprints().values()) {
                            totPyros++;
                            perSpecPyros++;
                        }
                        totIsols++;
                        perSpecIsols++;
                    }
                    totHosts++;
                    perSpecHosts++;
                }
                statsStream.printf("%d,%s,%d,%d,%d\n",specNum,s,perSpecHosts,perSpecIsols,perSpecPyros);
                totSpecs++;
                specNum++;
                perSpecHosts = 0;
                perSpecIsols = 0;
                perSpecPyros = 0;
            }
            statsStream.printf("%s,%d,%d,%d,%d\n","Total",totSpecs,totHosts,totIsols,totPyros);
        }
    }
