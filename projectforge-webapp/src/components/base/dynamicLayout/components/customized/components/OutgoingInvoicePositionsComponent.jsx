import React from 'react';
import { connect } from 'react-redux';
import { Button, UncontrolledCollapse } from 'reactstrap';
import { DynamicLayoutContext } from '../../../context';

function OutgoingInvoicePositionsComponent() {
    const { data, callAction } = React.useContext(DynamicLayoutContext);

    const style64 = {
        width: 64,
    };

    const style105 = {
        width: 105,
    };

    const style315 = {
        width: 315,
    };

    const style838 = {
        width: 838,
    };

    const textareaStyle = {
        width: 315,
        minHeight: 2,
        height: 35.5333,
    };

    const addPosition = () => callAction({
        responseAction: {
            url: '/invoice/addPosition',
            targetType: 'POST',
        },
    });

    const displayKostZuweisungen = {

    };

    function loadPositions() {
        const positions = [];
        const { positionen } = data;

        if (positionen === undefined || positionen.length === 0) {
            return (
                <>
                    <div>
                        <Button color="primary" id="toggler" style={{ marginBottom: '1rem' }}>Pos #1</Button>
                    </div>

                    <UncontrolledCollapse toggler="#toggler">

                        <div className="container-fluid">
                            <div className="row has-sub-rows">
                                <div className="col-sm-6 has-children first has-siblings">
                                    <div className="row">
                                        <div className="col-sm-3 first has-siblings">
                                            <div className="control-group vertical">
                                                <label className="control-label" htmlFor="idf8">Order</label>
                                                <div className="controls" style={style64}>
                                                    <input style={style64} type="text" value="" autoComplete="off" title="" className="text ac_input" />
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-sm-3 not-first has-siblings">
                                            <div className="control-group vertical">
                                                <label className="control-label" htmlFor="idf8">Quantity</label>
                                                <div className="controls" style={style64}>
                                                    <input style={style64} type="text" value="" autoComplete="off" title="" className="text ac_input" />
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-sm-3 not-first has-siblings">
                                            <div className="control-group vertical">
                                                <label className="control-label" htmlFor="idf8">Unit net price</label>
                                                <div className="controls" style={style64}>
                                                    <input style={style64} type="text" value="" autoComplete="off" title="" className="text ac_input" />
                                                </div>
                                            </div>
                                        </div>

                                        <div className="col-sm-3 not-first">
                                            <div className="control-group vertical">
                                                <label className="control-label" htmlFor="idf8">VAT</label>
                                                <div className="controls" style={style64}>
                                                    <input style={style64} type="text" value="" autoComplete="off" title="" className="text ac_input" />
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div className="col-sm-6 has-children not-first">
                                    <div className="row">
                                        <div className="col-sm-4 first has-siblings">
                                            <div className="control-group vertical">
                                                <label className="control-label">Net</label>
                                                <div className="controls">
                                                    <span id="id105">0.00 €</span>
                                                </div>
                                            </div>
                                        </div>

                                        <div className="col-sm-4 not-first has-siblings">

                                            <div className="control-group vertical">
                                                <label className="control-label">VAT amount </label>
                                                <div className="controls" style={style105}>
                                                    <span id="id106">0.00 €</span>
                                                </div>
                                            </div>
                                        </div>

                                        <div className="col-sm-4 not-first">
                                            <div className="control-group vertical">
                                                <label className="control-label">Gross </label>
                                                <div className="controls" style={style105}>
                                                    <span id="id107">0.00 €</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div className="row has-sub-rows">
                                <div className="col-sm-6 first has-siblings">
                                    <div className="control-group">
                                        <label className="control-label" htmlFor="idfc">Text</label>
                                        <div className="controls" style={style315}>
                                            <textarea
                                                id="idfc"
                                                maxLength="1000"
                                                className="autogrow"
                                                style={textareaStyle}
                                            />
                                        </div>
                                    </div>
                                </div>

                                <div className="col-sm-6 has-children not-first">
                                    <div className="row">
                                        <div className="col-sm-6 first has-siblings">
                                            <table className="costassignment" id="id108">
                                                <thead>
                                                    <tr>
                                                        <th>Cost 1</th>
                                                        <th>Cost 2</th>
                                                        <th>Net</th>
                                                        <th>Percent</th>
                                                    </tr>
                                                </thead>
                                                <tbody />
                                            </table>
                                        </div>

                                        <div className="col-sm-6 not-first">
                                            <button className="light">
                                                <span>Edit</span>
                                            </button>
                                            <span />
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div className="row">
                                <div className="col-sm-12 first">
                                    <button value="Do it!" type="submit" className="btn btn-xs btn-danger">Delete</button>
                                </div>
                            </div>

                            <div className="row">
                                <div className="col-sm-12 first">
                                    <div className="control-group">
                                        <label className="control-label" htmlFor="idfe">Period of performance</label>
                                        <div className="controls controls-row" style={style838}>
                                            <select>
                                                <option selected="selected" value="SEEABOVE">see above</option>
                                                <option value="OWN">own</option>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </UncontrolledCollapse>

                    <div className="row">
                        <button
                            value="Do it!"
                            type="submit"
                            className="btn btn-xs"
                            data-html="true"
                            rel="mytooltip-right"
                            title=""
                            data-original-title="Add invoice position"
                        >
                            Add
                        </button>
                    </div>

                </>
            );
        }

        for (let i = 0; i < positionen.length; i++) {
            const position = positionen[i];
            const net = position.menge * position.einzelNetto;
            const vat = net * position.vat;

            const { kostZuweisungen } = position;
            const kostZuweisungTable = [];

            if (kostZuweisungen !== undefined) {
                for (let k = 0; k < kostZuweisungen.length; k++) {
                    const kostZuweisung = kostZuweisungen[i];
                    kostZuweisungTable.push(
                        <tr>
                            <td>{kostZuweisung.kost1.formattedNumber}</td>
                            <td>{kostZuweisung.kost2.displayName}</td>
                            <td>{kostZuweisung.netto}</td>
                            <td>
                                {(kostZuweisung.netto / net) * 100}
                                %
                            </td>
                        </tr>,
                    );
                }
            }

            positions.push(
                <>
                    <div>
                        <Button color="primary" id="toggler" style={{ marginBottom: '1rem' }}>
                            Pos #
                            {i + 1}
                        </Button>
                    </div>

                    <UncontrolledCollapse toggler="#toggler">

                        <div className="container-fluid">
                            <div className="row has-sub-rows">
                                <div className="col-sm-6 has-children first has-siblings">
                                    <div className="row">
                                        <div className="col-sm-3 first has-siblings">
                                            <div className="control-group vertical">
                                                <label className="control-label" htmlFor="idf8">Order</label>
                                                <div className="controls" style={style64}>
                                                    <input style={style64} type="text" value="" autoComplete="off" title="" className="text ac_input" />
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-sm-3 not-first has-siblings">
                                            <div className="control-group vertical">
                                                <label className="control-label" htmlFor="idf8">Quantity</label>
                                                <div className="controls" style={style64}>
                                                    <input style={style64} type="text" value={position.menge} autoComplete="off" title="" className="text ac_input" />
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-sm-3 not-first has-siblings">
                                            <div className="control-group vertical">
                                                <label className="control-label" htmlFor="idf8">Unit net price</label>
                                                <div className="controls" style={style64}>
                                                    <input style={style64} type="text" value={position.einzelNetto} autoComplete="off" title="" className="text ac_input" />
                                                </div>
                                            </div>
                                        </div>

                                        <div className="col-sm-3 not-first">
                                            <div className="control-group vertical">
                                                <label className="control-label" htmlFor="idf8">VAT</label>
                                                <div className="controls" style={style64}>
                                                    <input style={style64} type="text" value={position.vat} autoComplete="off" title="" className="text ac_input" />
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div className="col-sm-6 has-children not-first">
                                    <div className="row">
                                        <div className="col-sm-4 first has-siblings">
                                            <div className="control-group vertical">
                                                <label className="control-label">Net</label>
                                                <div className="controls">
                                                    <span id="id105">
                                                        {net}
                                                        {' '}
                                                        €
                                                    </span>
                                                </div>
                                            </div>
                                        </div>

                                        <div className="col-sm-4 not-first has-siblings">

                                            <div className="control-group vertical">
                                                <label className="control-label">VAT amount </label>
                                                <div className="controls" style={style105}>
                                                    <span id="id106">
                                                        {vat}
                                                        {' '}
                                                        €
                                                    </span>
                                                </div>
                                            </div>
                                        </div>

                                        <div className="col-sm-4 not-first">
                                            <div className="control-group vertical">
                                                <label className="control-label">Gross </label>
                                                <div className="controls" style={style105}>
                                                    <span id="id107">
                                                        {net + vat}
                                                        {' '}
                                                        €
                                                    </span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div className="row has-sub-rows">
                                <div className="col-sm-6 first has-siblings">
                                    <div className="control-group">
                                        <label className="control-label" htmlFor="idfc">Text</label>
                                        <div className="controls" style={style315}>
                                            <textarea
                                                id="idfc"
                                                maxLength="1000"
                                                className="autogrow"
                                                style={textareaStyle}
                                            />
                                        </div>
                                    </div>
                                </div>

                                <div className="col-sm-6 has-children not-first">
                                    <div className="row">
                                        <div className="col-sm-6 first has-siblings">
                                            <table className="costassignment" id="id108">
                                                <thead>
                                                    <tr>
                                                        <th>Cost 1</th>
                                                        <th>Cost 2</th>
                                                        <th>Net</th>
                                                        <th>Percent</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    {kostZuweisungTable}
                                                </tbody>
                                            </table>
                                        </div>

                                        <div className="col-sm-6 not-first">
                                            <button className="light">
                                                <span>Edit</span>
                                            </button>
                                            <span />
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div className="row">
                                <div className="col-sm-12 first">
                                    <button value="Do it!" type="submit" className="btn btn-xs btn-danger">Delete</button>
                                </div>
                            </div>

                            <div className="row">
                                <div className="col-sm-12 first">
                                    <div className="control-group">
                                        <label className="control-label" htmlFor="idfe">Period of performance</label>
                                        <div className="controls controls-row" style={style838}>
                                            <select>
                                                <option selected="selected" value="SEEABOVE">see above</option>
                                                <option value="OWN">own</option>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </UncontrolledCollapse>
                </>,
            );
        }

        return (
            <>
                {positions}
                <div className="row">
                    <button
                        value="Do it!"
                        onClick={addPosition}
                        className="btn btn-xs"
                        data-html="true"
                        rel="mytooltip-right"
                        title=""
                        data-original-title="Add invoice position"
                    >
                        Add
                    </button>
                </div>
            </>
        );
    }

    return React.useMemo(
        () => loadPositions(),
    );
}

const mapStateToProps = ({ authentication }) => ({
    user: authentication.user,
});

export default connect(mapStateToProps)(OutgoingInvoicePositionsComponent);
