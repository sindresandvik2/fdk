import * as React from "react";
import * as ReactDOM from "react-dom";
import {SearchPage} from "./SearchPage";
import {App} from "./AppPage";
import { Router, Link, Route, browserHistory, IndexRoute } from 'react-router'

const Home = () => {
  return (
    <div>
      <Link to="search">Go to Search</Link>
    </div>
  )
}

ReactDOM.render((
<Router history={browserHistory}>
  <Route path="/datasets" component={SearchPage}>
    <Route path="*" component={SearchPage}/>
    <IndexRoute component={SearchPage}/>
  </Route>
</Router>
), document.getElementById('root'));