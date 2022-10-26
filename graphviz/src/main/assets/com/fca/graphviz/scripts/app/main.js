let VIEW_WIDTH = 600;
let VIEW_HEIGHT = 600;
let MIN_GRAPH_SIZE = 500;
let MIN_LEVEL_DIFF = 20;
let GRAPH_OFFSET = 100;

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
  nodeStroke = "#fff", // node stroke color
  nodeStrokeWidth = 1.5, // node stroke width, in pixels
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
      intent: n.intent
    })
  );

  links = d3.map(links, (_, i) => ({source: LS[i], target: LT[i]}));

  // Compute default domains.
  if (G && nodeGroups === undefined) nodeGroups = d3.sort(G);

  // Construct the scales.
  const color = nodeGroup == null ? null : d3.scaleOrdinal(nodeGroups, colors);

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
  if (G) node.attr("fill", ({index: i}) => color(G[i]));
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

  return Object.assign(svg.node(), {scales: {color}});
}

function showGraph(graph) {
    var chart = ForceGraph(graph, {
      nodeId: d => d.id,
      nodeGroup: d => d.group,
      nodeTitle: d => `${d.id}\n${d.group}`,
      linkStrokeWidth: l => Math.sqrt(l.value),
      width: VIEW_WIDTH,
      height: getHeight(graph.nodes) // a promise to stop the simulation when the cell is re-run
    });

    var root = document.getElementById("root");
    //the following shows it in a pop-up window, but the write() and html() functions should be what you need.
    root.innerHTML = '';
    root.appendChild(chart);
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
            showGraph(params.graph);
            break;
    }
}

//parseWebChannelMessage(JSON.parse('{"fn":"setGraph","params":{"graph":{"links":[{"source":"1","target":"2","value":1},{"source":"2","target":"3","value":1},{"source":"3","target":"0","value":1},{"source":"4","target":"5","value":1},{"source":"4","target":"8","value":1},{"source":"4","target":"9","value":1},{"source":"4","target":"11","value":1},{"source":"4","target":"15","value":1},{"source":"5","target":"2","value":1},{"source":"5","target":"6","value":1},{"source":"6","target":"3","value":1},{"source":"6","target":"7","value":1},{"source":"7","target":"0","value":1},{"source":"8","target":"1","value":1},{"source":"8","target":"10","value":1},{"source":"8","target":"12","value":1},{"source":"9","target":"13","value":1},{"source":"9","target":"18","value":1},{"source":"10","target":"14","value":1},{"source":"11","target":"6","value":1},{"source":"11","target":"12","value":1},{"source":"11","target":"13","value":1},{"source":"11","target":"16","value":1},{"source":"12","target":"3","value":1},{"source":"12","target":"14","value":1},{"source":"13","target":"7","value":1},{"source":"13","target":"19","value":1},{"source":"14","target":"0","value":1},{"source":"15","target":"16","value":1},{"source":"15","target":"18","value":1},{"source":"16","target":"17","value":1},{"source":"16","target":"19","value":1},{"source":"17","target":"0","value":1},{"source":"18","target":"10","value":1},{"source":"18","target":"19","value":1},{"source":"19","target":"14","value":1}],"nodes":[{"group":1,"id":"0","level":0},{"group":1,"id":"1","level":3},{"group":1,"id":"2","level":2},{"group":1,"id":"3","level":1},{"group":1,"id":"4","level":8},{"group":1,"id":"5","level":3},{"group":1,"id":"6","level":2},{"group":1,"id":"7","level":1},{"group":1,"id":"8","level":5},{"group":1,"id":"9","level":4},{"group":1,"id":"10","level":2},{"group":1,"id":"11","level":5},{"group":1,"id":"12","level":2},{"group":1,"id":"13","level":3},{"group":1,"id":"14","level":1},{"group":1,"id":"15","level":4},{"group":1,"id":"16","level":3},{"group":1,"id":"17","level":1},{"group":1,"id":"18","level":3},{"group":1,"id":"19","level":2}]}}}'))