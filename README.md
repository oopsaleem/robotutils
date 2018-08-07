# robotutils
Automatically exported from code.google.com/p/robotutils

I, myself don't know what the HECK is this project....


{
	// Place your global snippets here. Each snippet is defined under a snippet name and has a scope, prefix, body and 
	// description. Add comma separated ids of the languages where the snippet is applicable in the scope field. If scope 
	// is left empty or omitted, the snippet gets applied to all languages. The prefix is what is 
	// used to trigger the snippet and the body will be expanded and inserted. Possible variables are: 
	// $1, $2 for tab stops, $0 for the final cursor position, and ${1:label}, ${2:another} for placeholders. 
	// Placeholders with the same ids are connected.
	// Example:
	// "Print to console": {
	// 	"scope": "javascript,typescript",
	// 	"prefix": "log",
	// 	"body": [
	// 		"console.log('$1');",
	// 		"$2"
	// 	],
	// 	"description": "Log output to console"
	// }
    "For_Loop": {
        "prefix": "for",
        "body": [
          "for (const ${2:element} of ${1:array}) {",
          "\t$0",
          "}"
        ],
        "description": "For Loop"
		},
		"Print to console": {
			"scope": "javascript,typescript",
			"prefix": "console log",
			"body": [
				"console.log('$1');$0",
			],
			"description": "Log output to console"
		},
		"react_dummy_component": {
			"prefix": "import React class Component",
			"body": [
				"import React, { Component } from 'react';",
				"",
				"class ${1:MyComponent} extends Component {",
				"\tconstructor(props) {",
				"\t\tsuper(props);",
				"\t\t",
				"\t\tthis.state = {",
				"\t\t\t${2:var1}: ${3:[]},",
				"\t\t}",
				"\t}",
				"",
				"\trender() {",
				"\t\treturn (",
				"\t\t\t<${4:div}>",
				"\t\t\t\t$5",
				"\t\t\t</${4:div}>",
				"\t\t);",
				"\t}",
				"}",
				"",
				"export default ${1:MyComponent};",
				"",
			],
			"description": "Snippet: React class Component"
	},
	"react_dummy_function_component": {
		"prefix": "import React function Component",
		"body": [
			"import React from 'react';",
			"",
			"const ${1:MyComponent} = (props) => {",
			"\treturn props.children",
			"}",
			"",
			"export default ${1:MyComponent};",
			"",
		],
		"description": "Snippet: React function Component"
	},
	"react_error_class_component": {
		"prefix": "import React ErrorBoundry Component",
		"body": [
			"import React, { Component } from 'react';",
			"",
			"class ${1:ErrorBoundry} extends Component {",
			"\tconstructor(props) {",
			"\t\tsuper(props);",
			"\t\t",
			"\t\tthis.state = {",
			"\t\t\t${2:hasError}: ${3:false},",
			"\t\t}",
			"\t}",
			"",
			"\tcomponentDidCatch(error, info) {",
			"\t\tthis.setState({ hasError: true });",
			"\t}",
			"",
			"\trender() {",
			"\t\tif (this.state.hasError) {",
			"\t\t\treturn <h1>${4:Oops!}</h1>",
			"\t\t}",
			"\t",
			"\t\treturn this.props.children;",
			"\t}",
			"}",
			"",
			"export default ${1:ErrorBoundry};",
			"",
		],
		"description": "Snippet: React error Component"
	},
	"react_redux_reducer": {
		"prefix": "import reducer react-redux",
		"body": [
			"import { ${1:CHANGE_SEARCH_FIELD} } from '../actions/types';",
			"const initialState = {",
			"\t${2:searchField}: ${3:''}",
			"}",
			"",
			"export const searchList = (state=initialState, action={}) => {",
			"\tswitch (action.type) {",
			"\t\tcase ${1:CHANGE_SEARCH_FIELD}:",
			"\t\t\treturn { ...state, ${2:searchField}: action.payload };",
			"\t\t",
			"\t\tdefault:",
			"\t\t\treturn state;",
			"\t}",
			"}",
			""
		],
		"description": "Snippet: Reducer of React-Redux "
	},
	"react_redux_action": {
		"prefix": "import action react-redux",
		"body": [
			"import { ${1:CHANGE_SEARCH_FIELD} } from './types';",
			"",
			"export const ${2:setSearchField} = (${3:text}) => ({",
			"\ttype: ${1:CHANGE_SEARCH_FIELD},",
			"\tpayload: ${3:text}",
			"});",
			""
		],
		"description": "Snippet: action of React-Redux"
	},
	"react_redux_reducer_index": {
	"prefix": "import IndexReducer react REDUX",
	"body": [
		"import { combineReducers } from 'redux';",
		"import { searchList } from './searchReducer'",
		"",		
		"export default combineReducers({",
		"\t${1:searchRobots}: searchList,",
		"});",
		"$0"
		],
		"description": "Snippet: An index.js for a Reducers of React-REDUX"
	},
	"react_redux_action_types": {
		"prefix": "import ActionTypes react REDUX",
		"body": [
			"export const ${1:CHANGE_SEARCH_FIELD} = '${2:change_search_field}';",
			"$0"
		],
		"description": "Snippet: A types.js for actions of React-REDUX"
	},
	"react_redux_action_index": {
		"prefix": "import ActionIndex react REDUX",
		"body": [
			"export * from './${1:SearchActions}';",
			"$0"
		],
		"description": "Snippet: An index.js for Actions of React-REDUX"
	},
	"react_redux_action_pending_sucess_failed": {
		"prefix": "import Action Pending Success Faild react-redux",
		"body": [
			"import {$4${1:REQUEST_PENDING},$4${2:REQUEST_SUCCESS},$4${3:REQUEST_FAILED}$4} from '../action/types';",
			"",
			"export const ${5:requestData} = () => (dispatch) => {",
			"\tdispatch({ type: ${1:REQUEST_PENDING} });",
			"",
			"\tfetch('${6:https://jsonplaceholder.typicode.com/users}')",
			"\t\t\t.then(response => response.json())",
			"\t\t\t.then(data => dispatch({ type: ${2:REQUEST_SUCCESS}, payload: data }))",
			"\t\t\t.catch(error => dispatch({ type: ${3:REQUEST_FAILED}, payload: error }));",
			"}",
			"$0"
		],
		"description": "Snippet: Action of React-REDUX"
	},
	"react_redux_reducer_pending_sucess_failed": {
		"prefix": "import Reducer Pending Success Faild react-redux",
		"body": [
			"import {$4${1:REQUEST_PENDING},$4${2:REQUEST_SUCCESS},$4${3:REQUEST_FAILED}$4} from '../action/types';",
			"",
			"const initialState = {",
			"\tisPanding: false,",
			"\t${5:items}: [],",
			"\terror: ''",
			"}",
			"",
			"export const request_${5:items} = (state=initialState, action={}) => {",
			"\tswitch (action.type) {",
			"\t\tcase REQUEST_PENDING:",
			"\t\t\treturn { ...state, ${5:items}: [], isPanding: true, error: '' };",
			"",
			"\t\tcase REQUEST_SUCCESS: ",
			"\t\t\treturn { ...state, ${5:items}: action.payload, isPending: false, error: '' }",
			"",
			"\t\tcase REQUEST_FAILED:",
			"\t\t\treturn { ...state, ${5:items}: [], isPending: false, error: action.payload }",
			"\t\tdefault:",
			"\t\t\treturn state;",
			"\t}",
			"}",
			"$0"
		],
		"description": "Snippet: Reducer of React-REDUX"
	},
	"react_redux_component": {
		"prefix": "import react-redux Component",
		"body": [
			"import React, { Component } from 'react';",
			"import { connect } from 'react-redux';",
			"",
			"import { ${1:setSearchField, request${6:Items}} }  from '../actions';",
			"",
			"class ${2:App} extends Component {",
			"\tcomponentDidMount() {",
			"\t\tthis.props.onRequest${6:Items}();",
			"\t}",
			"",
			"\trender() {",
			"\t\tconst { ${3:searchField}, ${4:onSearchChange}, my${6:items}, isPending } = this.props;",
			"\t\tif (isPending) {",
			"\t\t\treturn <h1>Loading...</h1>",
			"\t\t}",
			"",
			"\t\tconst ${5:filteredList} = myList.filter(item => {",
			"\t\t\treturn item.name.toLowerCase().includes(${3:searchField}.toLowerCase());",
			"\t\t});",
			"",
			"\t\treturn (",
			"\t\t\t<div>",
			"\t\t\t\t<SearchBox searchChange={${4:onSearchChange}} />",
			"\t\t\t\t\t<ErrorBoundry>",
			"\t\t\t\t\t\t<CardList myList={${5:filteredList}}/>",
			"\t\t\t\t\t</ErrorBoundry>",
			"\t\t\t</div>",
			"\t\t\t);",
			"\t}",
			"}",
			"",
			"const mapStateToProps = state => {",
			"\treturn { ",
			"\t\t${3:searchField}: state.searchRobots.${3:searchField},",
			"\t\tmy${6:items}: state.request${6:Items}.${6:items}",
			"\t}",
			"}",
			"",
			"const mapDispatchToProps = (dispatch) => {",
			"\treturn { ",
			"\t\t${4:onSearchChange}: (event) => dispatch(${1:setSearchField}(event.target.value))",
			"\t\tonRequest${6:Items}: () => dispatch(request${6:Items}())",
			"\t}",
			"}",
			"",
			"export default connect(mapStateToProps, mapDispatchToProps)(${2:App});",
			""
		],
		"description": "Snippet: A React-REDUX aware component"
	},
}
