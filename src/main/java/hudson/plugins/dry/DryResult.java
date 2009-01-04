package hudson.plugins.dry;

import hudson.model.AbstractBuild;
import hudson.plugins.dry.parser.DuplicateCode;
import hudson.plugins.dry.util.AnnotationsBuildResult;
import hudson.plugins.dry.util.ParserResult;
import hudson.plugins.dry.util.model.JavaProject;

/**
 * Represents the results of the DRY analysis. One instance of this class is persisted for
 * each build via an XML file.
 *
 * @author Ulli Hafner
 */
public class DryResult extends AnnotationsBuildResult {
    /** Unique identifier of this class. */
    private static final long serialVersionUID = 2768250056765266658L;
    static {
        XSTREAM.alias("dry", DuplicateCode.class);
    }

    /**
     * Creates a new instance of <code>PmdResult</code>.
     *
     * @param build
     *            the current build as owner of this action
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     * @param result
     *            the parsed result with all annotations
     */
    public DryResult(final AbstractBuild<?, ?> build, final String defaultEncoding, final ParserResult result) {
        super(build, defaultEncoding, result);
    }

    /**
     * Creates a new instance of <code>PmdResult</code>.
     *
     * @param build
     *            the current build as owner of this action
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     * @param result
     *            the parsed result with all annotations
     * @param previous
     *            the result of the previous build
     */
    public DryResult(final AbstractBuild<?, ?> build, final String defaultEncoding, final ParserResult result, final DryResult previous) {
        super(build, defaultEncoding, result, previous);
    }

    /**
     * Returns a summary message for the summary.jelly file.
     *
     * @return the summary message
     */
    public String getSummary() {
        return ResultSummary.createSummary(this);
    }

    /** {@inheritDoc} */
    @Override
    public String getDetails() {
        String message = ResultSummary.createDeltaMessage(this);
        if (getNumberOfAnnotations() == 0 && getDelta() == 0) {
            message += "<li>" + Messages.DRY_ResultAction_NoWarningsSince(getZeroWarningsSinceBuild()) + "</li>";
            message += createHighScoreMessage();
        }
        return message;
    }

    /**
     * Creates a highscore message.
     *
     * @return a highscore message
     */
    private String createHighScoreMessage() {
        if (isNewZeroWarningsHighScore()) {
            long days = getDays(getZeroWarningsHighScore());
            if (days == 1) {
                return "<li>" + Messages.DRY_ResultAction_OneHighScore() + "</li>";
            }
            else {
                return "<li>" + Messages.DRY_ResultAction_MultipleHighScore(days) + "</li>";
            }
        }
        else {
            long days = getDays(getHighScoreGap());
            if (days == 1) {
                return "<li>" + Messages.DRY_ResultAction_OneNoHighScore() + "</li>";
            }
            else {
                return "<li>" + Messages.DRY_ResultAction_MultipleNoHighScore(days) + "</li>";
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    protected String getSerializationFileName() {
        return "dry-warnings.xml";
    }

    /** {@inheritDoc} */
    public String getDisplayName() {
        return Messages.DRY_ProjectAction_Name();
    }

    /**
     * Returns the results of the previous build.
     *
     * @return the result of the previous build, or <code>null</code> if no
     *         such build exists
     */
    @Override
    public JavaProject getPreviousResult() {
        DryResultAction action = getOwner().getAction(DryResultAction.class);
        if (action.hasPreviousResultAction()) {
            return action.getPreviousResultAction().getResult().getProject();
        }
        else {
            return null;
        }
    }

    /**
     * Returns whether a previous build result exists.
     *
     * @return <code>true</code> if a previous build result exists.
     */
    @Override
    public boolean hasPreviousResult() {
        return getOwner().getAction(DryResultAction.class).hasPreviousResultAction();
    }
}