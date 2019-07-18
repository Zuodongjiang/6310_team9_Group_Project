'use strict';

// tag::vars[]
const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');
// end::vars[]

// tag::app[]
class App extends React.Component {

	constructor(props) {
		super(props);
        this.state = { value: '', hidePathForm: '', hideSimulation: 'hidden', setting: { mowerStates: [], report: {}, map: { lawnStatus: [[]] }, mowerID: ''}};
        this.toggleButtonState = this.toggleButtonState.bind(this);
        this.toggleButtonStateStop = this.toggleButtonStateStop.bind(this);
        this.toggleButtonStateRun = this.toggleButtonStateRun.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    // get the value of an input field
    handleChange(event) {
        this.setState({ value: event.target.value });
    }
    // send POST request to server, get result from server
    handleSubmit(event) {
        event.preventDefault();
        client({method: 'POST', headers: { 'Content-Type': 'application/json' }, path: '/simulation', entity: { filePath: this.state.value } }).done(response => {
			this.setState({setting: response.entity, hidePathForm: 'hidden', hideSimulation: ''});
		});
    }


    // click next button and send PATCH request to server, get result from server
    toggleButtonState() {
        client({method: 'PATCH', path: '/next'}).done(response => {
            this.setState({setting: response.entity});
        });
        if (this.state.setting.mowerID == -1) {
            alert('Simulation Stoped, Reset Now');
        }
    }

    // click stop button and send DELET request to server, get result from server
    toggleButtonStateStop() {
        client({method: 'DELETE', path: '/stop'}).done(response => {
            this.setState({setting: response.entity});
        });
        //alert('Simulation Stoped, Reset Now');
    }

    // click fast-forward button and send PATCH request to server, get result from server
    toggleButtonStateRun() {
        client({method: 'PATCH', path: '/fast-forward'}).done(response => {
            this.setState({setting: response.entity});
        });
        //alert('Simulation Stoped, Reset Now');
    }


    // render html
	render() {
		return (
            <div>
                <div class={`container ${this.state.hidePathForm}`}>
                        <div class="row">
                            <div class="col-md-12 text-uppercase">
                                <h2 class="text-blue">Lawnmowers Simulation</h2>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-12">
                                <form onSubmit={this.handleSubmit}>
                                    <label>
                                        File Path:
                                        <input type="text" value={this.state.value} onChange={this.handleChange} />
                                    </label>
                                    <div class="row col-md-12">
                                        <input type="submit" value="Submit" />
                                    </div>
                                </form>
                            </div>
                        </div>
                </div>

                <div class={`container ${this.state.hideSimulation} bg-color`}>
                    <div class="strip">
                        <div class="row d-flex justify-content-center">
                            <h2 class="text-blue text-uppercase">Lawnmowers Simulation</h2>
                        </div>
                        <div class="row jd-flex justify-content-between">
                            <div>
                                <button onClick={this.toggleButtonState}> Next Step </button>
                            </div>
                            <div>
                                <button onClick={this.toggleButtonStateStop}>Stop & Restart</button>
                            </div>
                            <div>
                                <button onClick={this.toggleButtonStateRun}>Fast-Forward</button>
                            </div>
                        </div>
                    </div>
                    <div class="box">
                        <div class="d-flex justify-content-center">
                            <section class="float-left">
                                <Report report={this.state.setting.report}/>
                                <MowerList mowers={this.state.setting.mowerStates}
                                        mowerID={this.state.setting.mowerID}
                                />
                            </section>
                            <section class="float-right">
                                <Map map={this.state.setting.map.lawnStatus} />
                            </section>
                        </div>
                    </div>
                </div>
            </div>

		)
    }
}
//end::app[]

class Map extends React.Component{
	render() {
        var rowLength = this.props.map.length;
        // TODO row number
        var colLength = this.props.map[0].length;
        var arr = Array(colLength).fill(0);
        const rowX = arr.map((cell, index) =>
            <td class="tr-col"><h5>{index}</h5></td>
        );
        const rows = this.props.map.map((row, index) =>
            <div>
                <div class = "parent">
                    <td class="tr-col-left"><h5>{rowLength - 1 - index}</h5></td>
                    <tr class="map-tr float-right">
                        {row.map((cell) => {
                            return <td class = {cell.replace(/[^a-z]/gi, '')}>{cell.replace(/[^0-9]/gi, '')}</td>
                        })}
                    </tr>

                </div>
            </div>
		);
		return (
            <div>
                <h4 class="text-blue">Lawn Map</h4>
                <div >
                    <table class="map-table">
                        <tbody>
                        {rows}
                        <div class = "parent">
                            <tr class="map-tr float-right">
                                <td class="tr-col-left"></td>
                                {rowX}
                            </tr>
                        </div>
                        </tbody>
                    </table>
                </div>
            </div>
		)
	}
}

// tag::mower-list[]
class MowerList extends React.Component{
	render() {
		const mowers = this.props.mowers.map(mower =>
			<Mower mower={mower}
            />);
        var mowerID = this.props.mowerID;

		return (
            <div>
                <h4 class="text-blue">Mower States</h4>
                <div class="row">
                    <div class="col-md-12 table-responsive">
                        <div class="tableFixHead">
                            <table class="table table-bordered">
                                <thead class="thead-light">
                                    <tr>
                                        <th scope="col">Mower ID</th>
                                        <th scope="col">Mower Status</th>
                                        <th scope="col">Energy Level</th>
                                        <th scope="col">Stalled Turns</th>
                                    </tr>
                                </thead>
                                <tbody>
                                {mowers}

                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
		)
	}
}

// tag::mower[]
class Mower extends React.Component{
	render() {
        // TODO: highlight row
        var mowerID = this.props.mowerID;
        //var mower_id = this.props.mower.mower_id;
        // var isColored = Boolean(mowerID == mower_id);
		return (
			<tr>
				<td>{this.props.mower.mower_id}</td>
				<td>{this.props.mower.mowerStatus}</td>
				<td>{this.props.mower.energyLevel}</td>
                <td>{this.props.mower.stallTurn}</td>
			</tr>
		)
	}
}
// end::mower[]

// tag::report[]
class Report extends React.Component{
    render() {
        return (
            <div>
                <h4 class="text-blue">Summery Info</h4>
                <div class="row">
                    <div class="col-md-12 table-responsive">
                        <table class="table table-bordered">
                            <thead class="thead-light">
                                <tr>
                                    <th scope="col">Initial Grass</th>
                                    <th scope="col">Grass Cut</th>
                                    <th scope="col">Grass Left</th>
                                    <th scope="col">Turns Done</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr >
                                    <td>{this.props.report.initalGrassCount}</td>
                                    <td>{this.props.report.cutGrassCount}</td>
                                    <td>{this.props.report.grassRemaining}</td>
                                    <td>{this.props.report.turnCount}</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        )
    }
}
// end::report[]

// tag::render[]
ReactDOM.render(
	<App />,
	document.getElementById('react')
)
// end::render[]