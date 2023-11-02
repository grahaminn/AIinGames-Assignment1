import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

def load_experiment_data(experiment_dir, agent_names):
    n_agents = len(agent_names)
    data = pd.read_csv(f"experiments/{experiment_dir}/results/GAME_OVER.csv")
    n_games = len(data)
    results = np.zeros((n_games, n_agents))
    
    for i in range(n_agents):
        player_data = data[f"FinalScore(Player-{i})"]
        results[:, i] = player_data.to_numpy()
    
    return results

def plot_experiment_results(experiment_dir, agent_names, colors, title, save_filename=None):
    results = load_experiment_data(experiment_dir, agent_names)
    
    bplot = plt.boxplot(results, patch_artist=True)
    
    plt.title(title)
    plt.ylabel("Final Score")
    plt.xlabel("Agent")
    plt.xticks(ticks=[i+1 for i in range(len(agent_names))], labels=agent_names)
    
        
    for patch, color in zip(bplot['boxes'], colors):
        patch.set_facecolor(color)    
    
    plt.show()
    if save_filename is not None:
        savefig(save_filename, bbox_inches='tight', dpi=500)

    
    
if __name__ == "__main__":
    colors = ['lightblue', 'lightgreen', 'pink']

    plot_experiment_results('thompsonVsGroupM', ['UCB1', 'Thompson'], colors[0:2], 'Final Score Thompson Sampling vs UCB1 (500 games)')