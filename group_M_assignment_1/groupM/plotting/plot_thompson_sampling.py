import pandas as pd
import json
import numpy as np
import matplotlib.pyplot as plt
import scipy.stats as stats
from IPython.core.pylabtools import figsize

x = np.linspace(18, 45,1000)
norm = stats.norm

def plot_sockets(actions, iter, legend=False):
  plt.title(f"{iter} Iterations")    
  
  ymax = 0    
  for index, action in enumerate(actions):
    n_visitis = action[0]
    mu = action[1]
    v = action[3] / (action[2] + 1)
    print(f"Action {index} - N({mu},{round(np.sqrt(v), 2)})")
    # get the PDF of the socket using its estimates
    y = norm.pdf(x, mu, np.sqrt(v) )
    if legend:
        p = plt.plot(x, y, lw=2, label = f'action {index}')
    else:
        p = plt.plot(x, y, lw=2)
    c = p[0].get_markeredgecolor()    
    # plt.fill_between(x, y, 0, color=c, alpha=0.2 )    
    plt.legend()
    plt.autoscale(tight=False)

    plt.vlines(mu, 0, y[1:].max(), colors = c, linestyles = "--", lw = 2)    

    ymax = max( ymax, y[1:].max()*1.05 )

  axes = plt.gca()
  axes.set_ylim([0,ymax])    

def plot_nodes(iterations, iteration_actions):
    figsize(15.0, 14)
    for j, it in enumerate(iterations):
        plt.subplot(1 + (len(iterations) // 2), 2, j+1)         
        plt.subplots_adjust(hspace = 0.7)       
    
        plot_sockets(iteration_actions[j], it, legend=j == 0) 

def import_data(path, player_name):
    COLUMNS = ["Round", "Turn", "DataJson"]
    data = pd.read_csv(path)
    data = data[data["PlayerName"] == player_name]
    data = data[COLUMNS]
    return data

"""
    returns a numpy array with node data for a given round, turn and set of iterations
    round - integer round
    turn - integer turn
    iterations - list of string representing mcts iterations to parse
"""
def parse_turn_data(data, round, turn):
    data = data[data["Round"] == round]
    data = data[data["Turn"] == turn]
    data = data["DataJson"]
    json_data = list(data.apply(json.loads))[0]
    iterations = list(json_data.keys())
    n_actions = len(json_data[iterations[0]])
    iteration_data = np.zeros((len(iterations), n_actions, 4))

    for i, it in enumerate(iterations):
        actions = json_data[it]
        for a, action in enumerate(actions):
            iteration_data[i, a, 0] = action["nVisits"] 
            iteration_data[i, a, 1] = action["mean"] 
            iteration_data[i, a, 2] = action["alpha"] 
            iteration_data[i, a, 3] = action["beta"] 

    int_iterations = np.array([int(it) for it in iterations])
    sort_idx = int_iterations.argsort()
    return int_iterations[sort_idx], iteration_data[sort_idx]




if __name__ == "__main__":
    data = import_data('experiments/thompsonVsGroupMVisualise/results/ThompsonStats.csv', 'Thompson')
    iterations, iteration_actions = parse_turn_data(data,1, 8)
    iteration_indexes = np.round(np.linspace(0, len(iterations) - 1, 4)).astype(int)
    iterations = iterations[iteration_indexes]
    iteration_actions = iteration_actions[iteration_indexes]
    plot_nodes(iterations, iteration_actions)
    plt.show()
   