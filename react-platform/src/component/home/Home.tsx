import ApplicationList from "../application/ApplicationList";

const Home: React.FC<{}> = () => {
  return (
    <>
      <div style={{ height: "100vh" }}>
        <ApplicationList />
      </div>
    </>
  );
};

export default Home;
