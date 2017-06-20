package service.factory;

import service.Analyzer;
import service.FacebookTrolling;
import service.impl.AnalyzerImpl;
import service.impl.FacebookTrollingImpl;

public class ServiceFactory {

    /**
     * @return instance of {@link FacebookTrolling}.
     */
    public static FacebookTrolling getFacebookTrolling() {
        return new FacebookTrollingImpl();
    }

    /**
     * @return instance of {@link Analyzer}.
     */
    public static Analyzer getAnalyzer() {
        return new AnalyzerImpl();
    }
}
