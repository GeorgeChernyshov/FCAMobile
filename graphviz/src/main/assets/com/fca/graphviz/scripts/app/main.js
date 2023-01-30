let VIEW_WIDTH = 600;
let VIEW_HEIGHT = 600;
let MIN_GRAPH_SIZE = 500;
let MIN_LEVEL_DIFF = 20;
let GRAPH_OFFSET = 100;
let currentNode = undefined;
let DURATION = 750;
let FORCE_GRAPH = "FORCE_GRAPH";
let NODE_TRAVERSAL = "NODE_TRAVERSAL";
let currentMode = FORCE_GRAPH;
let currentGraph = undefined;

// Copyright 2021 Observable, Inc.
// Released under the ISC license.
// https://observablehq.com/@d3/force-directed-graph
function ForceGraph({
  nodes, // an iterable of node objects (typically [{id}, …])
  links // an iterable of link objects (typically [{source, target}, …])
}, {
  nodeId = d => d.id, // given d in nodes, returns a unique identifier (string)
  nodeGroup, // given d in nodes, returns an (ordinal) value for color
  nodeGroups, // an array of ordinal values representing the node groups
  nodeTitle, // given d in nodes, a title string
  nodeFill = "currentColor", // node stroke fill (if not using a group color encoding)
  nodeStroke = "#888", // node stroke color
  nodeStrokeWidth = 1, // node stroke width, in pixels
  nodeStrokeOpacity = 1, // node stroke opacity
  nodeRadius = 5, // node radius, in pixels
  nodeStrength,
  linkSource = ({source}) => source, // given d in links, returns a node identifier string
  linkTarget = ({target}) => target, // given d in links, returns a node identifier string
  linkStroke = "#999", // link stroke color
  linkStrokeOpacity = 0.6, // link stroke opacity
  linkStrokeWidth = 1.5, // given d in links, returns a stroke width in pixels
  linkStrokeLinecap = "round", // link stroke linecap
  linkStrength,
  colors = d3.schemeTableau10, // an array of color strings, for the node groups
  width = VIEW_WIDTH, // outer width, in pixels
  height = getHeight(nodes), // outer height, in pixels
  invalidation // when this promise resolves, stop the simulation
} = {}) {
  // Compute values.
  const N = d3.map(nodes, nodeId).map(intern);
  const LS = d3.map(links, linkSource).map(intern);
  const LT = d3.map(links, linkTarget).map(intern);
  if (nodeTitle === undefined) nodeTitle = (_, i) => N[i];
  const T = nodeTitle == null ? null : d3.map(nodes, nodeTitle);
  const G = nodeGroup == null ? null : d3.map(nodes, nodeGroup).map(intern);
  const W = typeof linkStrokeWidth !== "function" ? null : d3.map(links, linkStrokeWidth);
  const L = typeof linkStroke !== "function" ? null : d3.map(links, linkStroke);

  // Replace the input nodes and links with mutable objects for the simulation.
  let diff = getLevelDiff(nodes);
  nodes = d3.map(nodes, (n, i) => ({
      id: N[i],
      fy: getFy(n.level),
      extent: n.extent,
      intent: n.intent,
      newObject: n.newObjectAdded,
      newAttribute: n.newAttributeAdded
    })
  );

  links = d3.map(links, (_, i) => ({source: LS[i], target: LT[i]}));

  // Compute default domains.
  if (G && nodeGroups === undefined) nodeGroups = d3.sort(G);

  // Construct the forces.
  const forceNode = d3.forceManyBody();
  const forceLink = d3.forceLink(links).id(({index: i}) => N[i]);
  if (nodeStrength !== undefined) forceNode.strength(nodeStrength);
  if (linkStrength !== undefined) forceLink.strength(linkStrength);

  const simulation = d3.forceSimulation(nodes)
      .force("link", forceLink)
      .force("charge", forceNode)
      .force("center",  d3.forceCenter())
      .on("tick", ticked);

  const svg = d3.create("svg")
      .attr("width", width)
      .attr("height", height)
      .attr("viewBox", [-width / 2, -height / 2, width, height])
      .attr("style", "max-width: 100%; height: auto; height: intrinsic;");

  const link = svg.append("g")
      .attr("stroke", typeof linkStroke !== "function" ? linkStroke : null)
      .attr("stroke-opacity", linkStrokeOpacity)
      .attr("stroke-width", typeof linkStrokeWidth !== "function" ? linkStrokeWidth : null)
      .attr("stroke-linecap", linkStrokeLinecap)
    .selectAll("line")
    .data(links)
    .join("line");

  const node = svg.append("g")
      .attr("fill", nodeFill)
      .attr("stroke", nodeStroke)
      .attr("stroke-opacity", nodeStrokeOpacity)
      .attr("stroke-width", nodeStrokeWidth)
    .selectAll("circle")
    .data(nodes)
    .join("circle")
      .attr("r", nodeRadius)
      .attr("extent", function(d) { return d.extent })
      .attr("intent", function(d) { return d.intent })
      .on("click", click)
      .call(drag(simulation));



  if (W) link.attr("stroke-width", ({index: i}) => W[i]);
  if (L) link.attr("stroke", ({index: i}) => L[i]);
  if (G) node.attr("fill", function(d, i) {
    var topColor = d.newAttribute ? "#37c" : "#7cf";
    var bottomColor = d.newObject ? "#333" : "#bbb";
    var attrName = "grad" + i;
    var grad = svg.append("defs").append("linearGradient").attr("id", attrName)
      .attr("x1", "0%").attr("x2", "0%").attr("y1", "100%").attr("y2", "0%");
    grad.append("stop").attr("offset", "50%").style("stop-color", bottomColor);
    grad.append("stop").attr("offset", "50%").style("stop-color", topColor);

    return "url(#"+ attrName +")";
  });
  if (T) node.append("title").text(({index: i}) => T[i]);
  if (invalidation != null) invalidation.then(() => simulation.stop());

  function getLevelDiff(nodes) {
    let nodeLevels = nodes.map(node => node.level);
    let maxLevel = Math.max.apply(null, nodeLevels);
    if (maxLevel < 2) return MIN_LEVEL_DIFF;

    return Math.max(MIN_LEVEL_DIFF, MIN_GRAPH_SIZE / (maxLevel));
  }

  function getFy(level) {
    return (level * diff) - ((height - GRAPH_OFFSET) / 2);
  }

  function intern(value) {
    return value !== null && typeof value === "object" ? value.valueOf() : value;
  }

  function click(d, i) {
    currentNode = i;
  	ClickListener.onNodeClicked(JSON.stringify(i))
  }

  function ticked() {
    link
      .attr("x1", d => d.source.x)
      .attr("y1", d => d.source.y)
      .attr("x2", d => d.target.x)
      .attr("y2", d => d.target.y);

    node
      .attr("cx", d => d.x)
      .attr("cy", d => d.y);
  }

  function drag(simulation) {    
    function dragstarted(event) {
      if (!event.active) simulation.alphaTarget(0.3).restart();
      DragListener.onDragStarted();
      event.subject.fx = event.subject.x;
      event.subject.fy = event.subject.y;
    }
    
    function dragged(event) {
      event.subject.fx = event.x;
      event.subject.fy = event.y;
    }
    
    function dragended(event) {
      if (!event.active) simulation.alphaTarget(0);
      DragListener.onDragEnded();
    }
    
    return d3.drag()
      .on("start", dragstarted)
      .on("drag", dragged)
      .on("end", dragended);
  }

  return Object.assign(svg.node());
}

function NodeTraverseGraph({
  nodes, // an iterable of node objects (typically [{id}, …])
  links // an iterable of link objects (typically [{source, target}, …])
}, {
  nodeId = d => d.id, // given d in nodes, returns a unique identifier (string)
  nodeGroup, // given d in nodes, returns an (ordinal) value for color
  nodeGroups, // an array of ordinal values representing the node groups
  nodeTitle, // given d in nodes, a title string
  nodeFill = "currentColor", // node stroke fill (if not using a group color encoding)
  nodeStroke = "#888", // node stroke color
  nodeStrokeWidth = 1, // node stroke width, in pixels
  nodeStrokeOpacity = 1, // node stroke opacity
  nodeRadius = 5, // node radius, in pixels
  nodeStrength,
  linkSource = ({source}) => source, // given d in links, returns a node identifier string
  linkTarget = ({target}) => target, // given d in links, returns a node identifier string
  linkStroke = "#999", // link stroke color
  linkStrokeOpacity = 0.6, // link stroke opacity
  linkStrokeWidth = 1.5, // given d in links, returns a stroke width in pixels
  linkStrokeLinecap = "round", // link stroke linecap
  linkStrength,
  colors = d3.schemeTableau10, // an array of color strings, for the node groups
  width = VIEW_WIDTH, // outer width, in pixels
  height = getHeight(nodes), // outer height, in pixels
  invalidation // when this promise resolves, stop the simulation
} = {}) {
  // Compute values.
  const N = d3.map(nodes, nodeId).map(intern);
  const LS = d3.map(links, linkSource).map(intern);
  const LT = d3.map(links, linkTarget).map(intern);
  if (nodeTitle === undefined) nodeTitle = (_, i) => N[i];
  const T = nodeTitle == null ? null : d3.map(nodes, nodeTitle);
  const G = nodeGroup == null ? null : d3.map(nodes, nodeGroup).map(intern);
  const W = typeof linkStrokeWidth !== "function" ? null : d3.map(links, linkStrokeWidth);
  const L = typeof linkStroke !== "function" ? null : d3.map(links, linkStroke);

  var diagonal = d3.linkVertical()
    .x(function(d) { return d.x; })
    .y(function(d) { return d.y; });

  function intern(value) {
    return value !== null && typeof value === "object" ? value.valueOf() : value;
  }

  function click(d, i) {
    function toNode(i) {
      return {
        id: i.id,
        extent: i.extent,
        intent: i.intent
      }
    }
    ClickListener.onNodeClicked(JSON.stringify(toNode(i)));
    currentNode = i;
    visualizeNewLayout();
  }

  function addLink( conceptS, conceptD ) {
  	if( !conceptS.children ) {
      conceptS.children = [];
    }
    conceptS.children.push( conceptD );

    if( !conceptD.parents ) {
      conceptD.parents = [];
    }
    conceptD.parents.push( conceptS );
  }

  function visualizeNewLayout() {
   	computeNodePositions();
   	update();
  }

  function computeNodePositions() {
   	currentNode.x = 0;
   	currentNode.y = 0;
   	if(currentNode.parents) {
      placeNodesHorizontally(currentNode.parents, -2.5 * height / 8);
    }
    if(currentNode.children) {
      placeNodesHorizontally(currentNode.children, 2.5 * height / 8);
    }
  }

  function update() {
   	// Compute the new tree layout.
   	var nodes = [];
   	var links = [];
   	if(currentNode.parents) {
   	  nodes = nodes.concat(currentNode.parents);
      currentNode.parents.forEach(function(n){
        links.push({source:n,target:currentNode});
      });
   	}
    if(currentNode.children) {
      nodes = nodes.concat(currentNode.children);
      currentNode.children.forEach(function(n){
    	links.push({source:currentNode,target:n});
      });
    }
    nodes.push(currentNode);

    updateData(nodes,links);
  }

  // Updates the picture w.r.t. the data
  function updateData(nodes,links) {
    var node = svg.selectAll("circle")
      .data(nodes, function(d) { return d.id; });

    var nodeEnter = node.enter()
      .append("circle")
        .attr("fill", nodeFill)
        .attr("stroke", nodeStroke)
        .attr("stroke-opacity", nodeStrokeOpacity)
        .attr("stroke-width", nodeStrokeWidth)
        .attr("r", nodeRadius)
        .attr("extent", function(d) { return d.extent })
        .attr("intent", function(d) { return d.intent })
        .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
        .on("click", click);

    if (G) nodeEnter.attr("fill", function(d, i) {
        var topColor = d.newAttribute ? "#37c" : "#7cf";
        var bottomColor = d.newObject ? "#333" : "#bbb";
        var attrName = "grad" + i;
        var grad = svg.append("defs").append("linearGradient").attr("id", attrName)
          .attr("x1", "0%").attr("x2", "0%").attr("y1", "100%").attr("y2", "0%");
        grad.append("stop").attr("offset", "50%").style("stop-color", bottomColor);
        grad.append("stop").attr("offset", "50%").style("stop-color", topColor);

        return "url(#"+ attrName +")";
    });

    // Transition nodes to their new position.
    var nodeUpdate = node.transition()
      .duration(DURATION)
      .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });

    if (G) nodeUpdate.attr("fill", function(d, i) {
        var topColor = d.newAttribute ? "#37c" : "#7cf";
        var bottomColor = d.newObject ? "#333" : "#bbb";
        var attrName = "grad" + i;
        var grad = svg.append("defs").append("linearGradient").attr("id", attrName)
          .attr("x1", "0%").attr("x2", "0%").attr("y1", "100%").attr("y2", "0%");
        grad.append("stop").attr("offset", "50%").style("stop-color", bottomColor);
        grad.append("stop").attr("offset", "50%").style("stop-color", topColor);

        return "url(#"+ attrName +")";
    });

    // Transition exiting nodes to the parent's new position.
    var nodeExit = node.exit()
      //.attr("transform", function(d) { return "translate(" + prevNode.x + "," + prevNode.y + ")"; })
      .remove();

    var link = svg.selectAll("path")
      .data(links, function(d) { return d.source.id + d.target.id * 100000; });

    var linkEnter = link.enter().append("path")
      .attr("stroke", typeof linkStroke !== "function" ? linkStroke : null)
      .attr("stroke-opacity", linkStrokeOpacity)
      .attr("stroke-width", typeof linkStrokeWidth !== "function" ? linkStrokeWidth : null)
      .attr("stroke-linecap", linkStrokeLinecap)
      .attr("d", diagonal)
      .style("fill", "none");

    var linkUpdate = link.transition()
      .duration(DURATION)
      .attr("d", diagonal);

    var linkExit = link.exit()
                   //      .attr("d", function(d) {
                   //        var o = {x: prevNode.x, y: prevNode.y};
                   //    	return diagonal({source: o, target: o});
                   //      })
      .remove();
  }

  // Places nodes horizontally
  function placeNodesHorizontally(nodes, y)
  {
  	var step = width/nodes.length;
  	var lastNodeX = (-step * (nodes.length - 1)) / 2;
  	nodes.forEach(function(n){
  		n.x = lastNodeX;
  		lastNodeX += step;
  		n.y = y;
  		if( step < nodeRadius * 2.5 ) {
  			const verticalPlace=3 * height / 8;
  			n.y +=  Math.random() * verticalPlace - verticalPlace / 2;
  		}
  	});
  }

  const svg = d3.create("svg")
        .attr("width", width)
        .attr("height", height)
        .attr("viewBox", [-width / 2, -height / 2, width, height])
        .attr("style", "max-width: 100%; height: auto; height: intrinsic;");

  // Replace the input nodes and links with mutable objects for the simulation.
  nodes = d3.map(nodes, (n, i) => ({
      id: N[i],
      extent: n.extent,
      intent: n.intent,
      newObject: n.newObjectAdded,
      newAttribute: n.newAttributeAdded
    })
  );

  links = d3.map(links, (_, i) => ({source: LS[i], target: LT[i]}));

  links.forEach(function(arc){
      addLink(nodes[arc.source], nodes[arc.target]);
  });

  if (currentNode === undefined) currentNode = nodes[0];
  else currentNode = nodes.find(node => node.id == currentNode.id);

  visualizeNewLayout()

  return Object.assign(svg.node());
}

function showGraph() {
    var svg = createSvg();
    var root = document.getElementById("root");
    //the following shows it in a pop-up window, but the write() and html() functions should be what you need.
    root.innerHTML = '';
    root.appendChild(svg);
}

function createSvg() {
    if (currentMode == FORCE_GRAPH) {
      return ForceGraph(currentGraph, {
          nodeId: d => d.id,
          nodeGroup: d => d.group,
          nodeTitle: d => `${d.id}\n${d.group}`,
          linkStrokeWidth: l => Math.sqrt(l.value),
          width: VIEW_WIDTH,
          height: getHeight(currentGraph.nodes) // a promise to stop the simulation when the cell is re-run
        });
    } else {
      return NodeTraverseGraph(currentGraph, {
          nodeId: d => d.id,
          nodeGroup: d => d.group,
          nodeTitle: d => `${d.id}\n${d.group}`,
          linkStrokeWidth: l => Math.sqrt(l.value),
          width: VIEW_WIDTH,
          height: VIEW_HEIGHT // a promise to stop the simulation when the cell is re-run
        });
    }
}

function getHeight(nodes) {
  let nodeLevels = nodes.map(node => node.level);
  let maxLevel = Math.max.apply(null, nodeLevels);
  return Math.max(VIEW_HEIGHT, (MIN_LEVEL_DIFF * maxLevel) + GRAPH_OFFSET);
}

function parseWebChannelMessage(message) {
    var params = message.params
    switch (message.fn) {
        case "setGraph":
        currentGraph = params.graph
            showGraph();
            break;

        case "setMode":
            currentMode = params.mode;
            showGraph();
            break;
    }
}

//parseWebChannelMessage(JSON.parse('{"fn":"setGraph","params":{"graph":{"links":[{"source":1,"target":2,"value":1},{"source":2,"target":3,"value":1},{"source":3,"target":0,"value":1},{"source":4,"target":5,"value":1},{"source":4,"target":8,"value":1},{"source":4,"target":9,"value":1},{"source":4,"target":11,"value":1},{"source":4,"target":15,"value":1},{"source":5,"target":2,"value":1},{"source":5,"target":6,"value":1},{"source":6,"target":3,"value":1},{"source":6,"target":7,"value":1},{"source":7,"target":0,"value":1},{"source":8,"target":1,"value":1},{"source":8,"target":10,"value":1},{"source":8,"target":12,"value":1},{"source":9,"target":13,"value":1},{"source":9,"target":18,"value":1},{"source":10,"target":14,"value":1},{"source":11,"target":6,"value":1},{"source":11,"target":12,"value":1},{"source":11,"target":13,"value":1},{"source":11,"target":16,"value":1},{"source":12,"target":3,"value":1},{"source":12,"target":14,"value":1},{"source":13,"target":7,"value":1},{"source":13,"target":19,"value":1},{"source":14,"target":0,"value":1},{"source":15,"target":16,"value":1},{"source":15,"target":18,"value":1},{"source":16,"target":17,"value":1},{"source":16,"target":19,"value":1},{"source":17,"target":0,"value":1},{"source":18,"target":10,"value":1},{"source":18,"target":19,"value":1},{"source":19,"target":14,"value":1}],"nodes":[{"extent":[],"group":1,"id":0,"impact":0.4,"level":5,"newAttributeAdded":false,"newObjectAdded":true,"stab":1.0},{"extent":["fish leech","bream","frog"],"group":1,"id":1,"impact":0.4,"intent":["needs water","lives in water","can move"],"level":2,"newAttributeAdded":false,"newObjectAdded":false,"stab":0.5},{"extent":["bream","frog"],"group":1,"id":2,"impact":0.4,"intent":["needs water","lives in water","can move","has limbs"],"level":3,"newAttributeAdded":false,"newObjectAdded":true,"stab":0.5},{"extent":["frog"],"group":1,"id":3,"impact":0.4,"intent":["needs water","lives in water","can move","has limbs","lives on land"],"level":4,"newAttributeAdded":false,"newObjectAdded":true,"stab":0.5},{"extent":["fish leech","bream","frog","dog","water weeds","reed","bean","corn"],"group":1,"id":4,"impact":0.4,"intent":["needs water"],"level":0,"newAttributeAdded":true,"newObjectAdded":true,"stab":0.71875},{"extent":["bream","frog","dog"],"group":1,"id":5,"impact":0.4,"intent":["needs water","has limbs"],"level":1,"newAttributeAdded":false,"newObjectAdded":true,"stab":0.25},{"extent":["frog","dog"],"group":1,"id":6,"impact":0.4,"intent":["needs water","has limbs","lives on land"],"level":2,"newAttributeAdded":false,"newObjectAdded":true,"stab":0.25},{"extent":["dog"],"group":1,"id":7,"impact":0.4,"intent":["needs water","has limbs","lives on land","monocotyledon","breast feeds"],"level":3,"newAttributeAdded":false,"newObjectAdded":true,"stab":0.5},{"extent":["fish leech","bream","frog","water weeds","reed"],"group":1,"id":8,"impact":0.4,"intent":["needs water","lives in water"],"level":1,"newAttributeAdded":false,"newObjectAdded":true,"stab":0.625},{"extent":["dog","water weeds","reed","corn"],"group":1,"id":9,"impact":0.4,"intent":["needs water","monocotyledon"],"level":1,"newAttributeAdded":false,"newObjectAdded":true,"stab":0.25},{"extent":["water weeds","reed"],"group":1,"id":10,"impact":0.4,"intent":["needs water","lives in water","monocotyledon","needs chlorophyll"],"level":3,"newAttributeAdded":false,"newObjectAdded":true,"stab":0.5},{"extent":["frog","dog","reed","bean","corn"],"group":1,"id":11,"impact":0.4,"intent":["needs water","lives on land"],"level":1,"newAttributeAdded":false,"newObjectAdded":true,"stab":0.53125},{"extent":["frog","reed"],"group":1,"id":12,"impact":0.4,"intent":["needs water","lives in water","lives on land"],"level":2,"newAttributeAdded":false,"newObjectAdded":true,"stab":0.25},{"extent":["dog","reed","corn"],"group":1,"id":13,"impact":0.4,"intent":["needs water","lives on land","monocotyledon"],"level":2,"newAttributeAdded":true,"newObjectAdded":false,"stab":0.375},{"extent":["reed"],"group":1,"id":14,"impact":0.4,"intent":["needs water","lives in water","lives on land","monocotyledon","needs chlorophyll"],"level":4,"newAttributeAdded":false,"newObjectAdded":true,"stab":0.5},{"extent":["water weeds","reed","bean","corn"],"group":1,"id":15,"impact":0.4,"intent":["needs water","needs chlorophyll"],"level":1,"newAttributeAdded":false,"newObjectAdded":false,"stab":0.25},{"extent":["reed","bean","corn"],"group":1,"id":16,"impact":0.4,"intent":["needs water","lives on land","needs chlorophyll"],"level":2,"newAttributeAdded":false,"newObjectAdded":true,"stab":0.375},{"extent":["bean"],"group":1,"id":17,"impact":0.4,"intent":["needs water","lives on land","needs chlorophyll","dyocletedon"],"level":3,"newAttributeAdded":false,"newObjectAdded":true,"stab":0.5},{"extent":["water weeds","reed","corn"],"group":1,"id":18,"impact":0.4,"intent":["needs water","monocotyledon","needs chlorophyll"],"level":2,"newAttributeAdded":false,"newObjectAdded":true,"stab":0.25},{"extent":["reed","corn"],"group":1,"id":19,"impact":0.4,"intent":["needs water","lives on land","monocotyledon","needs chlorophyll"],"level":3,"newAttributeAdded":false,"newObjectAdded":true,"stab":0.5}]}}}'));
//parseWebChannelMessage(JSON.parse('{"fn":"setMode","params":{"mode":"NODE_TRAVERSAL"}}'));