import './progressBar.css'

const ProgressBar = (props) => {
    const { bgcolor, page, numPages } = props;
    const completed = Math.round((page + 1) * 100 / numPages);

    // const containerStyles = {
    //     height: 20,
    //     width: '80%',
    //     backgroundColor: "#e0e0de",
    //     borderRadius: 30,
    //     "margin-top": 30,
    //     "margin-bottom": 30,
    //     border: "gray 1px solid",
    //     "max-width": "960px"
    // }

    const fillerStyles = {
        height: '100%',
        width: `${completed}%`,
        backgroundColor: bgcolor,
        borderRadius: 'inherit',
        textAlign: 'right'
    }

    const labelStyles = {
        padding: 5,
        color: 'white',
        fontWeight: 'bold',
        fontSize: '10px',
        alignItems: 'center'
    }

    return (
        <div className={"containerStyles"}>
            <div style={fillerStyles}>
                <span className={"labelStyles"}>{`${completed}%`}</span>
            </div>
        </div>
    );
};

export default ProgressBar;