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
        this.state = { value: '', hidePathForm: '', hideSimulation: 'hidden', setting: { mowerStates: [], report: {}, map: { lawnStatus: [[]] }}};
        this.toggleButtonState = this.toggleButtonState.bind(this);
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
            console.log(this.state)
        });
    }
    // render html
	render() {
		return (
            <div>
                <div class={`container ${this.state.hidePathForm}`}>
                    <form onSubmit={this.handleSubmit}>
                        <label>
                        File Path:
                        <input type="text" value={this.state.value} onChange={this.handleChange} />
                        </label>
                        <input type="submit" value="Submit" />
                    </form>
                </div>

                <div class={`container ${this.state.hideSimulation}`}>

                    <div class="row">
                        <div class="col-md-6 text-uppercase">
                            <h3 class="text-blue">Lawnmowers Simulation</h3>
                        </div>
                        <section class="col-md-2">
                            <button onClick={this.toggleButtonState}> Next Step </button>
                        </section>

                        <section class="col-md-2">
                            <button type="button" onClick="alert('Hello world!')">Stop & Restart</button>
                        </section>

                        <section class="col-md-2">
                            <button type="button" onClick="alert('Hello world!')">Fast-Forward</button>
                        </section>
                    </div>
                    <div class="row">
                        <section class="col-md-5">
                            <Report report={this.state.setting.report}/>
                            <MowerList mowers={this.state.setting.mowerStates}/>
                        </section>
                        <section class="col-md-7">
                            <Map map={this.state.setting.map.lawnStatus} />
                        </section>

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
        const rows = this.props.map.map((row, index) =>
            <div class = "parent">
                <h4 class="float-left">{rowLength - 1 - index}</h4>
                <tr class="map-tr float-right">
                    {row.map((cell) => {

                            return <td class = {cell}></td>

                    })}
                </tr>
            </div>

		);
		return (
            <div>
                <h4 class="text-blue">Lawn Map</h4>
                <div >


                    <table class="map-table">
                        <tbody>
                        {rows}
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
			<Mower mower={mower}/>
		);
		return (
            <div>
                <h4 class="text-blue">Mower States</h4>
                <div class="row">
                    <div class="col-md-12 table-responsive table-striped">
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
                                <tr>
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