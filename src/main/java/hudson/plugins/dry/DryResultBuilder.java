package hudson.plugins.dry;

import hudson.model.AbstractBuild;
import hudson.plugins.dry.util.ParserResult;

/**
 * Creates a new DRY result based on the values of a previous build and the
 * current project.
 *
 * @author Ulli Hafner
 */
public class DryResultBuilder {
    /**
     * Creates a result that persists the DRY information for the
     * specified build.
     *
     * @param build
     *            the build to create the action for
     * @param result
     *            the result containing the annotations
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     * @return the result action
     */
    public DryResult build(final AbstractBuild<?, ?> build, final ParserResult result, final String defaultEncoding) {
        Object previous = build.getPreviousBuild();
        while (previous instanceof AbstractBuild<?, ?>) {
            AbstractBuild<?, ?> previousBuild = (AbstractBuild<?, ?>)previous;
            DryResultAction previousAction = previousBuild.getAction(DryResultAction.class);
            if (previousAction != null) {
                return new DryResult(build, defaultEncoding, result, previousAction.getResult());
            }
            previous = previousBuild.getPreviousBuild();
        }
        return new DryResult(build, defaultEncoding, result);
    }
}
