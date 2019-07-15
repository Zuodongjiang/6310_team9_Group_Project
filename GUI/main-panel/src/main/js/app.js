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
        this.state = {setting: { mowerStates: [], report: {}, map: { lawnStatus: [[]] }}};
        this.toggleButtonState = this.toggleButtonState.bind(this);
	}

	componentDidMount() {
		client({method: 'POST', path: '/simulation'}).done(response => {
			this.setState({setting: response.entity});
		});
	}

    toggleButtonState() {
        client({method: 'PATCH', path: '/next'}).done(response => {
            this.setState({setting: response.entity});
            console.log(this.state)
        });
    }

	render() {
		return (
            <div class="container">
                <header class="row">
                    <div class="col-md-12 text-uppercase">
                    <h3 class="text-blue">Lawnmowers Simulation</h3>
                    </div>
                </header>

                <div class="row">
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

                <MowerList mowers={this.state.setting.mowerStates}/>

                <Report report={this.state.setting.report}/>
                <Map map={this.state.setting.map.lawnStatus} />
            </div>
		)
    }
}
//end::app[]

class Map extends React.Component{
	render() {
		const rows = this.props.map.map(row =>
			<tr class="map-tr">
                {row.map(cell => {
                    return <td class = {cell}>{cell}</td>
                })}
            </tr>
		);
		return (
            <div>
                <h4 class="text-blue">Map</h4>
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
                    <div class="col-md-6 table-responsive">
                        <div class="tableFixHead">
                            <table class="table">
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
                    <div class="col-md-6 table-responsive">
                        <table class="table">
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