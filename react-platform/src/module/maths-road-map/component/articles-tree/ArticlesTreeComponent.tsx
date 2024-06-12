import React from "react";

import "reactflow/dist/style.css";
import { ArticleEdge } from "../../service/request/ReplaceRoadMapTreeRequest";

// const initialNodes = [
//   // { id: "1", position: { x: 0, y: 0 }, data: { label: "1" } },
//   // { id: "2", position: { x: 0, y: 100 }, data: { label: "2" } },
// ];

export interface Props {
  edges: Array<ArticleEdge>;
}

// function getNodes(edges: ArticleEdge[]): any[] {
//   const nodes: Array<any> = [];
//   edges.forEach((edge) => {
//     if (!nodes.some((el) => el.id === `${edge.prevId}`)) {
//     }
//   });
//   return
// }

function getEdges(edges: ArticleEdge[]): any[] {
  return edges.map((e) => {
    return {
      id: `${e.prevId - e.nextId}`,
      source: `${e.prevId}`,
      target: `${e.nextId}`,
    };
  });
}

const ArticlesTreeComponent: React.FC<Props> = ({ edges }: Props) => {
  return <></>;
  // const nodes: Array<any> = getNodes(edges);
  // return (
  //   <div style={{ width: "100vw", height: "100vh" }}>
  //     <ReactFlow nodes={[]} edges={getEdges(edges)} />
  //   </div>
  // );
};

export default ArticlesTreeComponent;
