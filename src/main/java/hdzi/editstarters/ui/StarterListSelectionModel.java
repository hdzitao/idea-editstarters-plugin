package hdzi.editstarters.ui;

public class StarterListSelectionModel extends EditStartersSelectionModel {
    private final StarterListRenderer renderer;

    public StarterListSelectionModel(StarterListRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void setSelectionInterval(int index0, int index1) {
        if (this.renderer != null && this.renderer.getDisableIndex().contains(index0)) {
            return;
        }

        super.setSelectionInterval(index0, index1);
    }
}
