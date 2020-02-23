package test.ui;

import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.ui.SnipersTableModel.Column;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SnipersTableModelTest {
    @Mock TableModelListener listener;
    @InjectMocks SnipersTableModel model;

    @BeforeEach
    void setUp() {
        model.addTableModelListener(listener);
    }

    @Test
    void hasEnoughColumns() {
        assertThat(model.getColumnCount(), equalTo(Column.values().length));
    }

    @Test
    void itNotifiesListenerWhenSniperStatusChanged() {
        model.sniperStateChanged(
                new SniperSnapshot("item-id", 5555, 6666, SniperState.JOINING)
        );

        verify(listener).tableChanged(argThat(matchesFirstRowChangedEvent()));
    }

    private ArgumentMatcher<TableModelEvent> matchesFirstRowChangedEvent() {
        TableModelEvent firstRowChangedEvent = new TableModelEvent(model, 0);

        return tableModelEvent ->
                tableModelEvent.getColumn() == firstRowChangedEvent.getColumn() &&
                        tableModelEvent.getFirstRow() == firstRowChangedEvent.getFirstRow() &&
                        tableModelEvent.getLastRow() == firstRowChangedEvent.getLastRow() &&
                        tableModelEvent.getType() == firstRowChangedEvent.getType();
    }

    @Test
    void itUpdatesTheModelWhenSniperStatusChanged() {
        model.sniperStateChanged(
                new SniperSnapshot("item-id", 5555, 6666, SniperState.BIDDING)
        );

        assertColumnEquals(Column.ITEM_IDENTIFIER, "item-id");
        assertColumnEquals(Column.LAST_PRICE, 5555);
        assertColumnEquals(Column.LAST_BID, 6666);
        assertColumnEquals(Column.SNIPER_STATE, "Bidding");
    }

    private <T> void assertColumnEquals(Column column, T expected) {
        final int rowIndex = 0;
        final int columnIndex = column.ordinal();
        assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
    }

    @ParameterizedTest(name = "Column {0}")
    @EnumSource(Column.class)
    void itSetsUpColumnHeaderBasedOnColumnEnumNames(Column column) {
        assertEquals(column.name, model.getColumnName(column.ordinal()));
    }
}